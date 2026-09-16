# Bloque 8 — Notificaciones in-app + rol de administrador (🟢 opcionales)

Los dos últimos ítems opcionales que tenían una decisión de diseño pendiente. Ambos se resolvieron con la opción acotada/recomendada.

## 1. Notificaciones in-app

**Por qué in-app y no email:** no había servidor SMTP en el stack tecnológico del enunciado, configurarlo hubiera significado inventar credenciales que no puedo probar de verdad, y un frontend real va a necesitar leer estas notificaciones desde la API de todas formas — así que in-app es lo que efectivamente se puede construir y probar ahora mismo.

**Qué se construyó:**
- Entidad [`Notificacion`](../backend/src/main/java/music/license/model/Notificacion.java) (`usuario` destinatario, `tipo`, `mensaje`, `leida`, `fecha`) + enum `TipoNotificacion` (`INVITACION_COLABORACION`, `ACEPTACION_COLABORACION`, `RECHAZO_COLABORACION`, `COMPRA_COMPLETADA`).
- [`NotificacionService`](../backend/src/main/java/music/license/service/NotificacionService.java): `crear` (interno, lo llaman otros servicios), `obtenerMias` (paginado), `contarNoLeidas`, `marcarLeida` (solo el destinatario).
- `NotificacionController`: `GET /api/notificaciones` (paginado, más recientes primero), `GET /api/notificaciones/no-leidas/contador`, `PATCH /api/notificaciones/{id}/leer`.
- Migración `V2__notificaciones.sql` (Flyway ya es el dueño del esquema desde el bloque anterior, así que cualquier entidad nueva pasa por una migración versionada, no por `ddl-auto`).

**Disparadores conectados** (los 4 eventos que pediste: invitar, aceptar/rechazar, compra completada):

| Evento | Quién recibe la notificación |
|---|---|
| Productor invita a un colaborador (`ColaboradorBeatService.crear`) | El colaborador invitado |
| Colaborador acepta (`aceptar`) | El productor dueño del beat |
| Colaborador rechaza (`rechazar`) | El productor dueño del beat |
| Compra se completa (`CompraService.checkout`) | El comprador (confirmación) **y** el productor del beat vendido (aviso de venta) |

No se agregó notificación para cierre/reapertura automática del acuerdo ni para la creación del beat — no estaban en el alcance que pediste, y sumarlas sin que las pidieras hubiera sido meter funcionalidad no solicitada.

## 2. Rol de administrador (alcance acotado)

**Qué significa "acotado" en la implementación:** `Rol.ADMIN` nuevo, con **solo tres endpoints de lectura**, sin ningún poder de escritura:

- `GET /api/admin/beats` — todos los beats de todos los productores, incluidos los borradores.
- `GET /api/admin/compras` — todas las compras de todos los usuarios.
- `GET /api/admin/acuerdos-creditos` — todos los acuerdos de créditos.

Los tres paginados, reutilizando los DTOs de respuesta que ya existían (`BeatResponse`, `CompraResponse`, `AcuerdoCreditosResponse`) — no hizo falta escribir DTOs nuevos.

**Por qué no le di poder de edición/borrado:** el resto del sistema define "quién puede tocar qué" en base a ser dueño del recurso (`AutorizacionUtil.exigirPropietario`, usado en 5 servicios distintos desde el Bloque 2). Si el admin pudiera editar/borrar cualquier cosa, cada uno de esos chequeos tendría que agregar una excepción especial ("...a menos que seas admin"), lo cual complica una lógica que hoy es simple y ya está bien testeada. Un admin de solo lectura (para "ver disputas y reportes", que es literalmente lo que pediste) no necesita eso.

**Cómo se le da el rol ADMIN a alguien:** a propósito, **no se puede** por `/api/auth/register` — `AuthService.registrar()` ahora rechaza explícitamente `rol=ADMIN` con un error claro. Dejar que cualquiera se autoregistre como admin sería un agujero de seguridad obvio. Hoy en día, la única forma de crear un admin es insertarlo directamente en la base de datos (`UPDATE usuarios SET rol = 'ADMIN' WHERE email = '...'` después de un registro normal). Si más adelante quieres un flujo formal para esto (por ejemplo, que un admin exisente pueda promover a otro usuario), es una extensión chica sobre lo que ya existe — avísame cuando lo necesites.

## Archivos tocados — resumen completo

**Nuevos:**
- `model/TipoNotificacion.java`, `model/Notificacion.java`
- `repository/NotificacionRepository.java`
- `service/NotificacionService.java`, `service/AdminService.java`
- `dto/notificacion/NotificacionResponse.java`
- `controller/NotificacionController.java`, `controller/AdminController.java`
- `db/migration/V2__notificaciones.sql`
- `src/test/java/.../NotificacionServiceTest.java`, `AdminServiceTest.java`

**Modificados:**
- `model/Rol.java` (nuevo valor `ADMIN`)
- `service/AuthService.java` (rechaza registro público con `rol=ADMIN`)
- `service/ColaboradorBeatService.java` (dispara notificaciones en `crear`/`aceptar`/`rechazar`)
- `service/CompraService.java` (dispara notificaciones en `checkout`)
- `src/test/java/.../ColaboradorBeatServiceTest.java`, `CompraServiceTest.java`, `AuthServiceTest.java` (mocks nuevos + verificaciones de las notificaciones disparadas)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 94 tests, 93 pasan
```

Sigue fallando solo `LicenseApplicationTests.contextLoads()` (Postgres local), no relacionado con este bloque. Igual que con `V1`, no pude ejecutar `V2__notificaciones.sql` contra un Postgres real por la misma razón (sin credenciales) — la revisé a mano contra la entidad `Notificacion` y coincide.

## Con esto se completan los 4 ítems opcionales que quedaban del análisis original

🟠 Flyway (bloque anterior) + 🟢 búsqueda por título (bloque anterior) + 🟢 notificaciones + 🟢 rol admin. Todo lo que quedaba en la lista de Fase 1 está resuelto o explícitamente descartado con una razón documentada.

## Sugerencia de mensaje de commit

```
feat(backend): notificaciones in-app y rol de administrador de solo lectura

- nueva entidad Notificacion + NotificacionService, conectada a invitar/aceptar/
  rechazar colaboracion y a completar una compra (avisa a comprador y productor)
- nuevo Rol.ADMIN con 3 endpoints de solo lectura (/api/admin/beats, /compras,
  /acuerdos-creditos) que ven todo sin restriccion de dueño; sin poder de escritura
- el registro publico (/api/auth/register) rechaza explicitamente rol=ADMIN
- V2__notificaciones.sql
```
