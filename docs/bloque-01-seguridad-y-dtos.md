# Bloque 1 — Seguridad de configuración + DTOs reales (🔴 imprescindible, parte 1 de 5)

## Objetivo de este bloque

Cerrar la base que necesitan todos los bloques siguientes: sacar el secreto real del repositorio y dejar de exponer las entidades JPA directamente en la API. Sin esto, cualquier regla de negocio que agreguemos después (split al 100%, checkout, etc.) se puede saltar por la puerta de atrás con un `PUT`/`POST` directo a una entidad.

No se tocó frontend, autorización por rol/ownership, ni la lógica de negocio de créditos/checkout — eso son los bloques 2 a 5.

## 1. Seguridad de configuración

**Archivo:** [`backend/src/main/resources/application.properties`](../backend/src/main/resources/application.properties)

- La contraseña real de Postgres (`Pablo7612`) y el usuario ya no están hardcodeados. Ahora se leen de variables de entorno:
  ```properties
  spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/license_music}
  spring.datasource.username=${DB_USERNAME:postgres}
  spring.datasource.password=${DB_PASSWORD}
  ```
  `DB_PASSWORD` **no tiene valor por defecto a propósito**: si no la defines, el arranque falla con un error claro en vez de intentar usar una contraseña vieja.
- `jwt.secret` y `jwt.expiration` también se movieron a variables de entorno, pero mantienen el valor actual como default (no es un secreto que ya se haya filtrado con datos reales, así que no rompe el arranque local si no configuras nada).

**Cómo correr el backend localmente ahora:**
```bash
export DB_PASSWORD=tu_password_de_postgres
./gradlew bootRun
```
o configúralo como variable de entorno en tu IDE (Run Configuration → Environment variables).

⚠️ **Pendiente de que tú decidas, no lo hice yo:** la contraseña `Pablo7612` ya quedó en el historial de git (commits anteriores) y posiblemente en GitHub si el repo es público. Te recomiendo:
1. Cambiar esa contraseña en tu Postgres real cuanto antes.
2. Si el repo es público, considerar limpiar el historial (`git filter-repo` o similar) — no lo hice porque reescribir historia es una operación destructiva que no toco sin que me lo pidas explícitamente.

## 2. DTOs reales en lugar de entidades JPA

Antes, **todos los controladores excepto `AuthController`** recibían y devolvían las entidades JPA (`Beat`, `Compra`, `ColaboradorBeat`, etc.) directamente. Eso permitía mass-assignment: cualquiera podía mandar `productor_id` de otra persona, forzar `estado=COMPLETADA` en una compra sin pagar, o `estado=ACEPTADO` en un colaborador sin haber aceptado nada. Además, los DTOs que ya existían (`BeatRequest`, `CompraResponse`, etc.) estaban vacíos — clases sin ningún campo, sin usar en ningún lado.

Se rellenaron y se conectaron a los 5 controladores CRUD:

| Módulo | Request | Response | Campos excluidos a propósito |
|---|---|---|---|
| Beat | `BeatRequest` (titulo, genero, bpm, urlPreview) | `BeatResponse` | `productor` (viene del usuario autenticado), `estado` (transición controlada, no editable a mano) |
| TipoLicencia | `TipoLicenciaRequest` (beatId, tipo, precio, condiciones) | `TipoLicenciaResponse` | — |
| Compra | `CompraRequest` (tipoLicenciaId) | `CompraResponse` | `comprador` (viene del usuario autenticado), `estado`/`fecha` (los pone el servidor) |
| ColaboradorBeat | `ColaboradorBeatRequest` (beatId, usuarioId, rol, porcentajePropuesto) | `ColaboradorBeatResponse` | `estado` (siempre arranca `PENDIENTE`, la aceptación es un paso aparte que llega en el bloque de créditos) |
| AcuerdoCreditos | `AcuerdoCreditosRequest` (beatId) | `AcuerdoCreditosResponse` | `estado`/`fechaCierre` (siempre arranca `ABIERTO`, el cierre lo dispara la lógica de negocio del bloque 3) |
| Usuario | — (sin creación manual, ver punto 3) | `UsuarioResponse` | — |

Cada `Response` tiene un factory estático `desde(entidad)` para mapear, siguiendo el mismo estilo simple (getters/setters manuales) que ya usaban `LoginRequest`/`RegisterRequest`, sin introducir Lombok ni una librería de mapeo nueva.

Los `Request` usan `jakarta.validation` (`@NotBlank`, `@NotNull`, `@Min`/`@Max`, `@DecimalMin`/`@DecimalMax`) y los controladores los reciben con `@Valid`. Como `GlobalExceptionHandler` ya manejaba `MethodArgumentNotValidException`, la validación ya devuelve un `400` con el detalle de cada campo sin tocar nada más.

**Archivos nuevos/reescritos:**
- `dto/beat/BeatRequest.java`, `dto/beat/BeatResponse.java`
- `dto/licencia/TipoLicenciaRequest.java`, `dto/licencia/TipoLicenciaResponse.java`
- `dto/compra/CompraRequest.java`, `dto/compra/CompraResponse.java`
- `dto/colaborador/ColaboradorBeatRequest.java`, `dto/colaborador/ColaboradorBeatResponse.java`
- `dto/acuerdo/AcuerdoCreditosRequest.java`, `dto/acuerdo/AcuerdoCreditosResponse.java`
- `dto/usuario/UsuarioResponse.java`
- **Eliminado:** `dto/usuario/UsuarioRequest.java` (quedaba vacío y sin uso, ver punto 3)

**Servicios que ganaron lógica de resolución de relaciones** (antes solo hacían `repository.save(entidadCompleta)`, ahora resuelven las FKs por id y arman la entidad):
- `TipoLicenciaService`: nuevo método `crear(TipoLicenciaRequest)` que busca el `Beat` por id; `actualizar` ahora recibe el DTO y no permite reasignar el beat.
- `ColaboradorBeatService`: nuevo método `crear(ColaboradorBeatRequest)` que busca `Beat` y `Usuario` por id, fuerza `estado=PENDIENTE`.
- `AcuerdoCreditosService`: nuevo método `crear(AcuerdoCreditosRequest)` que busca el `Beat` por id, fuerza `estado=ABIERTO` y `fechaCierre=null`.
- `CompraService`: nuevo método `crear(CompraRequest, Usuario comprador)` que busca el `TipoLicencia` por id, fuerza `comprador` = usuario autenticado, `fecha=ahora`, `estado=PENDIENTE`.
- `BeatService`: `actualizar` ahora recibe `BeatRequest` en vez de `Beat`, y ya no toca `estado` (antes se podía "publicar" un beat con un `PUT` cualquiera, sin que existiera ninguna validación detrás).

El usuario autenticado se obtiene con `@AuthenticationPrincipal Usuario usuario` en `BeatController` y `CompraController` — ya funcionaba así porque `JwtAuthenticationFilter` pone la entidad `Usuario` completa como principal, no hizo falta tocar seguridad para esto.

## 3. Endpoint roto eliminado: `POST /api/usuarios`

`UsuarioController.crear()` recibía un `Usuario` completo por JSON. Como `passwordHash` tiene `@JsonIgnore` (correctamente, para no filtrarlo en las respuestas), Jackson tampoco lo podía **leer** del JSON entrante — cualquier usuario creado por esa vía quedaba con `passwordHash = null` y nunca podía loguearse. Además duplicaba `/api/auth/register`, que sí hashea la contraseña correctamente.

Se eliminó el endpoint y el método `UsuarioService.guardar()` (quedaba sin ningún otro uso). `UsuarioController` ahora solo expone `GET /api/usuarios` y `GET /api/usuarios/{id}`, devolviendo `UsuarioResponse` (sin `passwordHash` ni nada sensible). La única forma de crear un usuario sigue siendo `/api/auth/register`.

## Archivos tocados — resumen completo

**Modificados:**
- `backend/src/main/resources/application.properties`
- `controller/BeatController.java`, `controller/TipoLicenciaController.java`, `controller/CompraController.java`, `controller/ColaboradorBeatController.java`, `controller/AcuerdoCreditosController.java`, `controller/UsuarioController.java`
- `service/BeatService.java`, `service/TipoLicenciaService.java`, `service/ColaboradorBeatService.java`, `service/AcuerdoCreditosService.java`, `service/CompraService.java`, `service/UsuarioService.java`
- `dto/beat/BeatRequest.java`, `dto/beat/BeatResponse.java`, `dto/licencia/TipoLicenciaRequest.java`, `dto/licencia/TipoLicenciaResponse.java`, `dto/compra/CompraRequest.java`, `dto/compra/CompraResponse.java`, `dto/colaborador/ColaboradorBeatRequest.java`, `dto/colaborador/ColaboradorBeatResponse.java`, `dto/acuerdo/AcuerdoCreditosRequest.java`, `dto/acuerdo/AcuerdoCreditosResponse.java`, `dto/usuario/UsuarioResponse.java`
- `src/test/java/music/license/service/BeatServiceTest.java`, `src/test/java/music/license/service/TipoLicenciaServiceTest.java`

**Nuevos:**
- `src/test/java/music/license/service/ColaboradorBeatServiceTest.java`
- `src/test/java/music/license/service/AcuerdoCreditosServiceTest.java`
- `src/test/java/music/license/service/CompraServiceTest.java`

**Eliminados:**
- `dto/usuario/UsuarioRequest.java` (quedaba vacío y sin uso)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
./gradlew test                          # 29 tests, 28 pasan
```

El único test que falla es `LicenseApplicationTests.contextLoads()`, que levanta el contexto de Spring completo contra un Postgres real en `localhost:5432`. Falló por `password authentication failed for user "postgres"` — es decir, la contraseña real de tu Postgres local no es la que estaba hardcodeada (`Pablo7612`). Esto **no es una regresión de este bloque**: ese test siempre dependió de tener un Postgres local con esas credenciales exactas corriendo; ahora simplemente lo hace explícito en vez de fallar en silencio con un secreto desactualizado. Corre `export DB_PASSWORD=<tu password real>` antes de `./gradlew test` para que pase.

## Qué falta (próximos bloques)

Este bloque **no agrega reglas de negocio nuevas** — solo cierra la puerta a los peores casos de mass-assignment (nadie puede forzar `estado=COMPLETADA`, `estado=ACEPTADO`, `estado=PUBLICADO` ni robarse el `productor_id`/`comprador_id` de otro). Lo que sigue, en orden:

1. **Bloque 2 — Autorización por rol/ownership**: hoy cualquier usuario autenticado (sea productor o comprador) puede editar/borrar cualquier beat, invitar colaboradores a beats ajenos, o ver todas las compras de todos. Falta `@PreAuthorize`/chequeo de ownership por endpoint.
2. **Bloque 3 — Lógica de créditos**: endpoint de aceptar/rechazar por colaborador, validación de que el split sume 100% antes de publicar, bloqueo del acuerdo y reapertura al modificar.
3. **Bloque 4 — Checkout + PDF**: transición `PENDIENTE → COMPLETADA`, generación del contrato en PDF.
4. **Bloque 5 — Catálogo con filtros + historial de compras del comprador**.

## Sugerencia de mensaje de commit

```
fix(backend): saca credenciales de BD del repo y reemplaza entidades JPA por DTOs en la API

- application.properties lee DB_PASSWORD/DB_USERNAME/JWT_SECRET de variables de entorno
- Beat, TipoLicencia, Compra, ColaboradorBeat, AcuerdoCreditos y Usuario ya no exponen
  las entidades JPA directamente: nuevos DTOs con validacion (@Valid) cierran el
  mass-assignment (productor_id, comprador_id, estado ya no se pueden forzar via API)
- elimina POST /api/usuarios, que quedaba roto (passwordHash null) y duplicaba /api/auth/register
- tests actualizados + nuevos para ColaboradorBeatService, AcuerdoCreditosService y CompraService
```
