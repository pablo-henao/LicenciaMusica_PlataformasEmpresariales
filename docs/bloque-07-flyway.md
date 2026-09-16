# Bloque 7 — Migraciones versionadas con Flyway (🟠 recomendado) + búsqueda por título (🟢 opcional)

> Este documento incluye dos cambios chicos hechos en la misma sesión: Flyway (el grueso del bloque) y, al final, la búsqueda por título en el catálogo — demasiado pequeña para justificar un `.md` aparte.

## Objetivo de este bloque

Hasta ahora, `spring.jpa.hibernate.ddl-auto=update` dejaba que Hibernate infiriera el esquema de la base de datos directamente de las entidades JPA en cada arranque. Funciona para desarrollo individual, pero tiene problemas reales para un equipo: nadie puede ver en un PR *qué* cambió en la base de datos, dos personas pueden terminar con esquemas ligeramente distintos en sus máquinas, y no hay forma de revertir un cambio de esquema de forma controlada.

## Qué se hizo

1. **Dependencias nuevas** en `build.gradle`: `org.flywaydb:flyway-core` y `org.flywaydb:flyway-database-postgresql` (Flyway 10+ separó el soporte de cada motor en un módulo aparte). Sin versión explícita: como el proyecto ya usa el plugin `io.spring.dependency-management`, Spring Boot elige automáticamente la versión de Flyway compatible con esta versión de Boot.
2. **`application.properties`**: `ddl-auto` pasó de `update` a `validate`. Ahora Hibernate **nunca** modifica el esquema — solo arranca y verifica que las entidades coincidan con lo que Flyway ya creó. Si algo no coincide, el arranque falla con un mensaje claro en vez de mutar la base en silencio.
3. **Nueva migración**: [`src/main/resources/db/migration/V1__esquema_inicial.sql`](../backend/src/main/resources/db/migration/V1__esquema_inicial.sql) — las 7 tablas actuales (`usuarios`, `beats`, `tipos_licencia`, `compras`, `colaboradores_beat`, `acuerdos_creditos`, `acuerdo_creditos_eventos`), con sus FKs, la restricción `UNIQUE` en `acuerdos_creditos.beat_id` (ya existía en la entidad) e índices en cada columna FK (Postgres no los crea automáticamente, a diferencia de la PK).

## Un hallazgo real de paso: `usuarios.email` no tenía restricción única en la base

Reescribir el esquema a mano obliga a mirar cada columna con lupa, y ahí apareció esto: `AuthService.registrar()` valida `usuarioRepository.existsByEmail(...)` en la capa de aplicación, pero la entidad `Usuario.email` **nunca tuvo `unique = true`**. Eso significa que dos registros concurrentes con el mismo email podían pasar ambos el chequeo `existsByEmail()` antes de que cualquiera de los dos hiciera commit, y terminar con dos usuarios con el mismo email en la base — una condición de carrera real, aunque de baja probabilidad.

Se agregó `@Column(unique = true, nullable = false)` a `Usuario.email` y la migración incluye `UNIQUE NOT NULL` en la columna. Ahora la base rechaza el duplicado aunque la carrera ocurra — la capa de aplicación sigue devolviendo el error amigable (`IllegalArgumentException`) en el caso normal, y la restricción de BD es la última línea de defensa para el caso raro.

## ⚠️ Paso manual que tienes que hacer vos: tu base de datos local ya tiene las tablas

Como venías corriendo con `ddl-auto=update`, tu Postgres local **ya tiene** las 7 tablas creadas por Hibernate. Si corres la app tal cual con Flyway habilitado, va a fallar en el arranque con algo como `relation "usuarios" already exists` — porque Flyway va a intentar crear tablas que ya están ahí.

Como este es un proyecto de desarrollo sin datos reales que proteger, la forma más simple y segura de resolverlo es **recrear la base local desde cero**:

```sql
DROP DATABASE license_music;
CREATE DATABASE license_music;
```

y luego arrancar la app normalmente (`DB_PASSWORD=... ./gradlew bootRun`) — Flyway va a crear las 7 tablas desde `V1__esquema_inicial.sql` y Hibernate las va a validar sin problema.

(La alternativa sería decirle a Flyway "asumí que V1 ya está aplicada" con `spring.flyway.baseline-on-migrate=true`, pero eso confía en que el esquema que ya tienes coincide exactamente con lo que escribí a mano — y ya vimos arriba que probablemente no, por lo del `UNIQUE` en `email`. Recrear la base es más lento un segundo, pero no deja dudas.)

## Verificación hecha — y su límite real

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 81 tests, 80 pasan
```

Los 80 tests que pasan son unitarios con Mockito — no tocan una base de datos real, así que no prueban la migración en sí. **No pude ejecutar la migración contra un Postgres real**: encontré una instalación de Postgres 16/17 en esta máquina, pero no tengo (ni te pedí) la contraseña real para conectarme — manejar esa credencial por este chat no es algo que deba hacer. Repasé cada columna de la migración contra cada entidad JPA a mano (ver tabla de arriba) y coinciden, pero te recomiendo que hagas tú la prueba real:

```bash
# 1. recrea la base local (ver seccion de arriba)
# 2. corre la app apuntando a esa base
DB_PASSWORD=<tu password> ./gradlew bootRun
```

Si `ddl-auto=validate` encuentra algo que no cuadra, el arranque falla con un mensaje que dice exactamente qué tabla/columna es — avísame si pasa eso y lo ajustamos.

## Archivos tocados — resumen completo

**Nuevo:**
- `db/migration/V1__esquema_inicial.sql`

**Modificados:**
- `build.gradle` (dependencias de Flyway)
- `application.properties` (`ddl-auto=validate`, `spring.flyway.enabled=true`)
- `model/Usuario.java` (`email` ahora `unique = true, nullable = false`)

## Extra: búsqueda por título en el catálogo (🟢 #15 del análisis original)

El filtro de catálogo (Bloque 5) solo tenía género (exacto) y rango de BPM. Se agregó `titulo` como filtro opcional adicional, con coincidencia parcial case-insensitive (`LIKE '%...%'`), igual de simple que el resto del filtro — no se justificaba traer un motor de búsqueda full-text (Postgres `tsvector`/Elasticsearch/etc.) para un catálogo de este tamaño.

```
GET /api/beats?titulo=sueños&genero=Reggaeton&bpmMin=90&bpmMax=100
```

**Archivos:** `repository/BeatRepository.java`, `service/BeatService.java`, `controller/BeatController.java` (todos ya tocados por el filtro de género/BPM del Bloque 5, solo se les agregó el parámetro `titulo`).

Sugerencia de commit para este cambio (aparte del de Flyway, son cosas independientes):
```
feat(backend): agrega busqueda por titulo (parcial) al catalogo de beats
```

## Sugerencia de mensaje de commit

```
feat(backend): migraciones versionadas con Flyway en vez de ddl-auto=update

- V1__esquema_inicial.sql crea las 7 tablas actuales con sus FKs e indices
- ddl-auto pasa de update a validate: Hibernate ya no modifica el esquema,
  solo lo valida contra lo que Flyway creo
- Usuario.email gana un UNIQUE a nivel de BD (antes solo se validaba en
  AuthService, lo que dejaba una condicion de carrera real en registros
  concurrentes con el mismo email)

NOTA para quien haga pull: hay que recrear la base de datos local
(DROP DATABASE + CREATE DATABASE) antes de arrancar, porque Flyway
va a fallar si las tablas ya existen de una corrida anterior con
ddl-auto=update. Ver docs/bloque-07-flyway.md.
```
