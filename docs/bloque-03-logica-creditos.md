# Bloque 3 — Lógica de negocio del split de créditos (🔴 imprescindible, parte 3 de 5)

## Objetivo de este bloque

Este es el módulo central del enunciado (sección 4.2) y, hasta ahora, era el que menos lógica real tenía: `ColaboradorBeat` y `AcuerdoCreditos` eran CRUD puro (Bloque 1) con autorización de dueño (Bloque 2), pero **nada** validaba el 100%, nadie podía aceptar/rechazar una invitación, y no existía ningún endpoint para publicar un beat. Este bloque implementa las 4 reglas de negocio que pide el enunciado:

1. La suma de porcentajes debe llegar a 100% antes de poder publicarse.
2. Aceptación individual del acuerdo por parte de cada colaborador invitado.
3. Bloqueo del acuerdo una vez todos aceptan.
4. Cualquier cambio reabre el acuerdo y reinicia las aceptaciones.

**Dejado fuera a propósito, para un bloque aparte:** el historial formal de "quién propuso, aceptó o modificó" (5ta viñeta de 4.2). En el análisis de fase 1 lo clasifiqué como 🟠 (recomendado, no imprescindible) porque hoy en día `AcuerdoCreditos.fechaCierre` y el estado de cada `ColaboradorBeat` ya dejan rastro del resultado final, aunque no de cada evento intermedio. Se puede armar con una entidad de eventos chica (`AcuerdoCreditosEvento`) sin tocar lo que se construye aquí — te aviso cuando lleguemos a los bloques 🟠 si quieres que lo prioricemos antes que catálogo/checkout.

## Decisión de diseño: beats solos vs. colaborativos

El enunciado dice: *"Cuando un beat... es resultado de una colaboración... el productor puede declarar a los colaboradores... y debe ser aceptado por todos antes de que el beat quede disponible para la venta."* Eso es condicional: si el beat **no** tiene colaboradores declarados (un beat 100% solo), la regla del 100%/aceptación no aplica — se publica libremente. Así quedó implementado:

- **Beat sin `ColaboradorBeat` asociados** → `publicar()` no exige ningún acuerdo, pasa directo a `PUBLICADO`.
- **Beat con al menos un `ColaboradorBeat`** → exige que exista un `AcuerdoCreditos` para ese beat y que esté `CERRADO`.

## Flujo completo, de punta a punta

1. Productor crea el beat (`BORRADOR`).
2. Productor invita colaboradores (`POST /api/colaboradores`), cada uno con su `porcentajePropuesto`.
3. Productor abre el acuerdo (`POST /api/acuerdos-creditos`, ya existía del Bloque 1) — queda `ABIERTO`.
4. Cada colaborador invitado acepta o rechaza **su propia fila** (nuevo: `PATCH /api/colaboradores/{id}/aceptar` o `/rechazar`) — solo el usuario invitado puede hacerlo, no el productor.
5. Después de cada aceptación, el sistema revisa automáticamente: ¿todos los `ColaboradorBeat` del beat están `ACEPTADO` **y** la suma de `porcentajePropuesto` da exactamente 100? Si sí, el `AcuerdoCreditos` pasa a `CERRADO` con `fechaCierre = ahora`. Si alguien rechazó, o la suma no cuadra (ej. un productor se equivocó y puso 90%), el acuerdo se queda `ABIERTO` aunque el resto ya haya aceptado — es una salvaguarda a propósito, no un bug: no tiene sentido cerrar un acuerdo con números que no suman 100.
6. Productor publica el beat (nuevo: `PATCH /api/beats/{id}/publicar`) — solo funciona si el acuerdo está `CERRADO` (o si el beat no tiene colaboradores).
7. Si el productor **después** invita a alguien más, edita el porcentaje de alguien, o quita a un colaborador — en cualquiera de los tres casos, si había un acuerdo `CERRADO`, se reabre (`ABIERTO`, `fechaCierre = null`) y **todos** los colaboradores (no solo el que cambió) vuelven a `PENDIENTE`, tengan que volver a aceptar la nueva propuesta. Esto es literal del enunciado ("cualquier cambio reabre... y reinicia las aceptaciones") y se aplicó también a los que ya habían rechazado, para darles oportunidad de reconsiderar con los números nuevos.

## Endpoints nuevos

| Endpoint | Quién puede | Qué hace |
|---|---|---|
| `PATCH /api/colaboradores/{id}/aceptar` | Solo el usuario invitado en esa fila | Marca `ACEPTADO`; si con esto se cumple 100%+todos aceptaron, cierra el acuerdo |
| `PATCH /api/colaboradores/{id}/rechazar` | Solo el usuario invitado en esa fila | Marca `RECHAZADO`; el acuerdo no puede cerrar mientras exista un rechazo |
| `PUT /api/colaboradores/{id}` | Solo el productor dueño del beat | Edita `rol`/`porcentajePropuesto` (no reasigna beat/usuario); reabre y reinicia si correspondía |
| `PATCH /api/beats/{id}/publicar` | Solo el productor dueño del beat | Valida la regla del 100%/acuerdo cerrado y pasa el beat a `PUBLICADO` |

`POST` (crear) y `DELETE` (eliminar) de `ColaboradorBeat`, que ya existían, ahora también disparan la reapertura/reinicio si corresponde.

## Otros cambios

- **`AcuerdoCreditosService.eliminar`** ahora rechaza borrar un acuerdo `CERRADO` (`409`, mensaje explicando que hay que agregar/editar/quitar un colaborador para reabrirlo primero) — evita borrar por accidente el registro de un cierre ya validado y potencialmente ligado a un beat publicado.
- **`GlobalExceptionHandler`** ahora mapea `IllegalStateException` → `409 Conflict` (antes esas excepciones cayan en el handler genérico de `500`, lo cual hubiera hecho ver un rechazo de negocio legítimo — "no puedes publicar, el acuerdo no está cerrado" — como si fuera un error interno del servidor).
- **`ColaboradorBeatRepository.findByBeatId`** y **`AcuerdoCreditosRepository.findByBeatId`** — nuevos métodos que necesita toda esta lógica para poder mirar "todos los colaboradores de este beat" y "el acuerdo de este beat" sin pasar por sus ids individuales.
- **`BeatService`** ahora depende también de `ColaboradorBeatRepository` y `AcuerdoCreditosRepository` (antes solo de `BeatRepository`) — es la única forma de que `publicar()` pueda validar la regla sin duplicar la lógica de cierre en dos sitios.

## Archivos tocados — resumen completo

**Modificados:**
- `service/ColaboradorBeatService.java` (reescrito: `actualizar`, `aceptar`, `rechazar`, reapertura automática en `crear`/`actualizar`/`eliminar`)
- `service/BeatService.java` (nuevo `publicar`, nuevas dependencias)
- `service/AcuerdoCreditosService.java` (`eliminar` bloquea acuerdos `CERRADO`)
- `controller/ColaboradorBeatController.java` (nuevo `PUT`, `PATCH /aceptar`, `PATCH /rechazar`)
- `controller/BeatController.java` (nuevo `PATCH /publicar`)
- `repository/ColaboradorBeatRepository.java`, `repository/AcuerdoCreditosRepository.java` (nuevo `findByBeatId`)
- `exception/GlobalExceptionHandler.java` (maneja `IllegalStateException` → 409)
- `src/test/java/.../BeatServiceTest.java`, `src/test/java/.../ColaboradorBeatServiceTest.java` (reescritos con el flujo completo: cierre automático, suma incorrecta, reapertura, rechazo)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 59 tests, 58 pasan
```

Sigue fallando solo `LicenseApplicationTests.contextLoads()`, por el mismo motivo de siempre (necesita tu Postgres local) — no relacionado con este bloque.

## Qué falta (próximos bloques, y lo que quedó pendiente explícitamente)

- **Bloque 4 — Checkout + PDF**: transición `PENDIENTE → COMPLETADA` de una compra, generación del contrato.
- **Bloque 5 — Catálogo con filtros + historial de compras** con paginación.
- **Pendientes del Bloque 2** (sin tocar, como pediste): si cerramos `GET /api/usuarios`, y si una compra `COMPLETADA` debería poder borrarse.
- **Pendiente de este bloque**: entidad de historial/auditoría formal del acuerdo de créditos (🟠).
- Nota menor: no agregué un endpoint para que un colaborador invitado liste "mis invitaciones pendientes" — hoy tendría que usar `GET /api/colaboradores` (sigue abierto a todos, sin filtrar) y buscar las suyas. Encaja mejor en el Bloque 5 junto con el resto de listados/filtros.

## Sugerencia de mensaje de commit

```
feat(backend): logica de negocio del split de creditos (aceptar/rechazar, 100%, bloqueo/reapertura)

- ColaboradorBeat: aceptar/rechazar por el colaborador invitado; cierre automatico
  del AcuerdoCreditos cuando todos aceptan y el split suma exactamente 100%
- crear/editar/quitar un colaborador reabre el acuerdo y reinicia todas las
  aceptaciones (incluidas las de otros colaboradores), como pide el enunciado
- Beat: nuevo endpoint publicar() que exige acuerdo cerrado solo si el beat
  tiene colaboradores declarados; los beats solos publican libremente
- AcuerdoCreditos ya cerrado no se puede eliminar
- GlobalExceptionHandler mapea IllegalStateException a 409 en vez de 500
```
