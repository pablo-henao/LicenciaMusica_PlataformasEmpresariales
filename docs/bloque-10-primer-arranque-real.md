# Bloque 10 — Primer arranque real contra Postgres (y 3 bugs que estaban escondidos)

Hasta hoy, en toda la vida del proyecto, nunca se había logrado correr el backend contra una base Postgres real: la contraseña documentada (`Pablo7612`) no autenticaba en ninguna instalación local disponible. Cada bloque anterior tuvo que documentar esa limitación y confiar únicamente en el test suite (con mocks) y en Flyway/Hibernate compilando en teoría.

Hoy se resolvió el acceso a Postgres (password reseteada localmente, documentado aparte, no es parte del repo) y, al arrancar el backend **por primera vez contra una base real**, aparecieron tres bugs que ningún test ni compilación podían detectar — porque todos requieren una base de datos real ejecutando SQL de verdad.

## Bug 1 — Flyway nunca se ejecutaba (el más grave)

`build.gradle` solo tenía `org.flywaydb:flyway-core` y `flyway-database-postgresql` — el motor de Flyway. Pero esta versión de Spring Boot (4.1) modularizó el *glue* que hace que Spring reconozca y dispare Flyway automáticamente en un artefacto aparte: **`org.springframework.boot:spring-boot-flyway`**, que nunca se agregó. Sin él, `FlywayAutoConfiguration` ni siquiera existe en el classpath — no es que fallara una condición, es que la clase no estaba. Resultado: en cualquier base de datos, con cualquier contraseña, Flyway jamás creó una sola tabla, y Hibernate (`ddl-auto=validate`) siempre iba a fallar con "missing table". Este bug llevaba deshabilitando el arranque real desde el Bloque 7 (cuando se introdujo Flyway), sin que nadie pudiera notarlo por la barrera de la contraseña.

**Fix:** agregar `implementation 'org.springframework.boot:spring-boot-flyway'` a `backend/build.gradle`.

## Bug 2 — `@Lob` en una columna que no es un LOB

`TipoLicencia.condiciones` (el texto de condiciones de una licencia, un párrafo como mucho) estaba anotado `@Lob`. En Postgres, Hibernate mapea `@Lob String` al tipo `oid` (large object binario), pero la migración de Flyway (correctamente) la creó como `TEXT`. Con Flyway funcionando por primera vez, Hibernate por fin llegó a validar el esquema y lo detectó: `wrong column type ... found [text], but expecting [oid]`.

**Fix:** quitar `@Lob` — un `String` simple ya mapea a texto, que es lo que la columna necesita.

## Bug 3 — `LOWER(:parametro)` con parámetro `null` rompía el catálogo

La consulta de catálogo (`BeatRepository.buscarCatalogo`) filtra género y título con patrones como `(:genero IS NULL OR LOWER(b.genero) = LOWER(:genero))`. Cuando el filtro no se usa (`:genero` es `null`, el caso normal al entrar a la tienda sin buscar nada), el driver de Postgres no podía inferir el tipo del parámetro dentro de `LOWER(?)` y adivinaba `bytea` en vez de texto — `ERROR: no existe la función lower(bytea)`, un 500 en cualquier carga del catálogo o del home.

**Fix:** cast explícito en el JPQL — `LOWER(CAST(:genero AS string))` y lo mismo para `:titulo`.

## Verificación real (no simulada) hecha hoy

Con el backend arriba de verdad y el frontend apuntándole:
- Registro de un productor → JWT persistido → interceptor adjuntando el header en cada llamada subsecuente (`/api/auth/me`, `/api/notificaciones/...`).
- Crear un beat (POST, validación de campos obligatorios en el cliente).
- Verlo en "Mis beats" (GET), editarlo (PUT), publicarlo (PATCH).
- Verlo aparecer en la Tienda pública (GET con filtros — el que rompía por el Bug 3).

Es la primera vez en el proyecto que este flujo se prueba de punta a punta contra datos reales, no contra mocks.

## Verificación de no-regresión

`DB_PASSWORD=dummy ./gradlew test` — 111 tests, 1 fallo (`contextLoads()`, que ahora falla por la contraseña dummy intencional del entorno de test, no por Flyway — antes fallaba por no poder conectar en absoluto). Los 110 tests restantes, sin cambios.

## Pendiente

- Hacer commit de `backend/build.gradle`, `TipoLicencia.java` y `BeatRepository.java` — sin el primero, el backend no arranca en **ningún** entorno, ni siquiera en la nube para la sustentación.
- Si van a desplegar en la nube para mañana, verificar que el mismo `spring-boot-flyway` esté disponible ahí también (ya está en el `build.gradle`, así que cualquier build fresco lo va a traer).
