# Bloque 6 — Cierra las dos decisiones pendientes del Bloque 2 y 3

Resuelve las dos preguntas que quedaron abiertas: qué hacer con `GET /api/usuarios` (fuga de emails) y si vale la pena construir el historial/auditoría del acuerdo de créditos que pide el enunciado (4.2, última viñeta). Ambas se resolvieron con la opción recomendada.

## 1. `GET /api/usuarios`: de "listar todo" a "buscar puntual"

**Antes:** `GET /api/usuarios` devolvía la lista completa de usuarios (nombre, email, rol) a cualquier autenticado — cualquiera podía enumerar el email de todos los demás.

**Ahora:**
- Se **eliminó** `GET /api/usuarios` (el listado completo).
- Se agregó `GET /api/usuarios/buscar?email=...` — devuelve **un solo usuario** por email exacto (404 si no existe). Resuelve el caso de uso real: el productor conoce el email de la persona que quiere invitar como colaborador y necesita su `id` para el `ColaboradorBeatRequest`.
- `GET /api/usuarios/{id}` se mantiene igual (consulta puntual por id ya conocido, no es una vía de enumeración).

La diferencia clave entre lo que se quitó y lo que se agregó: **enumerar** (dame todos) vs. **buscar** (dame uno que ya sé que existe). Lo segundo no es una fuga de datos en el mismo sentido — necesitas saber el email exacto de antemano.

**Archivos:** `service/UsuarioService.java` (quita `obtenerTodos`, agrega `buscarPorEmail`), `controller/UsuarioController.java` (quita `GET /`, agrega `GET /buscar`), nuevo `src/test/java/.../UsuarioServiceTest.java`.

## 2. Historial/auditoría del acuerdo de créditos

**Qué pide el enunciado (4.2, última viñeta):** *"Historial visible de quién propuso, aceptó o modificó el acuerdo de créditos."*

**Qué se construyó:** una entidad de auditoría nueva, [`AcuerdoCreditosEvento`](../backend/src/main/java/music/license/model/AcuerdoCreditosEvento.java) (`beat`, `usuario` que ejecutó la acción, `tipo`, `detalle`, `fecha`), con el enum [`TipoEventoAcuerdo`](../backend/src/main/java/music/license/model/TipoEventoAcuerdo.java): `ABIERTO`, `PROPUESTA`, `MODIFICACION`, `ACEPTACION`, `RECHAZO`, `ELIMINACION`, `CIERRE`, `REAPERTURA`.

Se conectó en cada uno de los puntos del flujo de créditos que ya existían del Bloque 3, sin cambiar ninguna regla de negocio, solo agregando el registro:

| Acción | Evento que queda |
|---|---|
| Productor abre el acuerdo (`AcuerdoCreditosService.crear`) | `ABIERTO` |
| Productor invita a un colaborador (`ColaboradorBeatService.crear`) | `PROPUESTA` |
| Productor edita rol/porcentaje (`actualizar`) | `MODIFICACION` |
| Colaborador acepta (`aceptar`) | `ACEPTACION` |
| Colaborador rechaza (`rechazar`) | `RECHAZO` |
| Productor quita a un colaborador (`eliminar`) | `ELIMINACION` |
| Se cierra automáticamente (100% + todos aceptaron) | `CIERRE` |
| Se reabre automáticamente por un cambio | `REAPERTURA` (solo si realmente estaba `CERRADO`; no genera ruido si ya estaba abierto) |

**Por qué el evento se referencia al `Beat` y no al `AcuerdoCreditos`:** un colaborador se puede proponer *antes* de que el productor abra formalmente el acuerdo (nada en el Bloque 1-3 obliga a abrirlo primero). Si el evento dependiera del `acuerdo_id`, esa primera propuesta no tendría dónde colgarse. Referenciarlo al beat evita ese problema de orden y de paso simplifica: siempre hay un beat, no siempre hay (todavía) un acuerdo.

**Nuevo endpoint:** `GET /api/beats/{id}/historial-creditos` — devuelve los eventos ordenados por fecha ascendente. Visibilidad: **solo el productor dueño del beat o alguno de los colaboradores invitados** (en cualquier estado, no solo los que ya aceptaron) — nadie más puede verlo. Esto respondía implícitamente la pregunta abierta del Bloque 5 sobre "qué puede ver un colaborador invitado".

## Archivos tocados — resumen completo

**Nuevos:**
- `model/TipoEventoAcuerdo.java`, `model/AcuerdoCreditosEvento.java`
- `repository/AcuerdoCreditosEventoRepository.java`
- `dto/acuerdo/AcuerdoCreditosEventoResponse.java`
- `src/test/java/.../UsuarioServiceTest.java`

**Modificados:**
- `service/UsuarioService.java`, `controller/UsuarioController.java`
- `service/ColaboradorBeatService.java` (registra evento en cada acción; `reabrirAcuerdoYReiniciarAceptaciones` ahora también recibe el usuario que disparó el cambio)
- `service/AcuerdoCreditosService.java` (registra `ABIERTO` al crear)
- `service/BeatService.java` (nuevo `obtenerHistorialCreditos`)
- `controller/BeatController.java` (`GET /{id}/historial-creditos`)
- `src/test/java/.../ColaboradorBeatServiceTest.java`, `AcuerdoCreditosServiceTest.java`, `BeatServiceTest.java` (mocks del nuevo repositorio + verificaciones de que los eventos correctos se registran)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 81 tests, 80 pasan
```

Un detalle real que encontró esta pasada de tests (no cosmético): `ColaboradorBeatService.eliminar` ahora lee `colaboradorBeat.getUsuario().getNombre()` para el detalle del evento `ELIMINACION`. Un test tenía un `ColaboradorBeat` de prueba sin `usuario` seteado (nunca hacía falta antes) y explotó con `NullPointerException` — lo cual en realidad *sirvió* como señal correcta: expuso que ese test no reflejaba un estado real de la entidad (`usuario_id` es `NOT NULL` en la base de datos, así que en producción esto nunca pasa). Se corrigió el test para que sea representativo, no el código de producción.

Sigue fallando solo `LicenseApplicationTests.contextLoads()` (Postgres local), no relacionado con este bloque.

## Con esto, el módulo de créditos (4.2) queda 100% cubierto contra el enunciado

Las 5 viñetas de 4.2 están implementadas: registro de colaboradores con rol (Bloque 1), suma debe llegar a 100% antes de publicar (Bloque 3), aceptación individual (Bloque 3), bloqueo + reapertura (Bloque 3), y ahora el historial (este bloque).

## Sugerencia de mensaje de commit

```
feat(backend): busqueda acotada de usuarios por email + historial de auditoria de creditos

- GET /api/usuarios (listado completo) se elimina; nuevo GET /api/usuarios/buscar?email=...
  devuelve un solo usuario exacto, sin exponer el directorio completo
- nueva entidad AcuerdoCreditosEvento: registra quien propuso/acepto/rechazo/modifico/
  cerro/reabrio el acuerdo de creditos de cada beat, enganchada en los puntos ya
  existentes de ColaboradorBeatService y AcuerdoCreditosService
- nuevo GET /api/beats/{id}/historial-creditos, visible solo para el productor
  dueño o los colaboradores invitados
```
