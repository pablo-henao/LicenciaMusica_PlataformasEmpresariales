# Bloque 9 — Cierre de los 3 huecos que quedaron sueltos

Último mini-bloque: cierra las tres cosas que fui anotando como "pendiente" en los docs de bloques anteriores pero que nunca formaron parte de una lista formal. Con esto no queda nada abierto en el backend.

## 1. Visibilidad de licencias, colaboradores y acuerdos de créditos

En el Bloque 5 cerré el hueco de que cualquier autenticado pudiera ver los beats en `BORRADOR` de otros productores (`GET /api/beats`, `GET /api/beats/{id}`). Pero nunca repliqué esa misma regla a los tres recursos que cuelgan de un beat:

- **`TipoLicencia`** (`/api/licencias`): antes cualquiera veía las licencias de *cualquier* beat, publicado o no. Ahora: visibles si el beat está `PUBLICADO` (es el catálogo de venta, tiene que ser público), o si sos el productor dueño del beat (para gestionar las licencias de tus propios borradores).
- **`ColaboradorBeat`** (`/api/colaboradores`) y **`AcuerdoCreditos`** (`/api/acuerdos-creditos`): antes cualquiera veía el split de porcentajes y el estado del acuerdo de créditos de cualquier beat. Esto es más sensible que el catálogo — no debería ser público ni siquiera para un beat ya publicado. Ahora: visibles solo para el productor dueño del beat o alguno de sus colaboradores declarados (el mismo criterio que ya usaba `GET /api/beats/{id}/historial-creditos` desde el Bloque 6).

**Refactor de paso:** la regla "sos el dueño o sos colaborador de este beat" estaba a punto de repetirse por tercera vez (ya existía inline en `BeatService.obtenerHistorialCreditos` del Bloque 6). Se extrajo a [`AutorizacionUtil.esDuenioOColaborador(...)`](../backend/src/main/java/music/license/security/AutorizacionUtil.java) — a diferencia de `exigirRol`/`exigirPropietario`, este devuelve `boolean` en vez de lanzar, porque cada llamador necesita decidir distinto qué excepción tirar: `ColaboradorBeatService`/`AcuerdoCreditosService` la usan para devolver `404` (ocultar que el recurso existe, mismo criterio que los beats en borrador), mientras que `BeatService.obtenerHistorialCreditos` la sigue usando para devolver `403` (ese endpoint ya estaba probado y documentado así desde el Bloque 6, no había razón para cambiarle el código de estado).

**Patrón consistente con Beats:** igual que con `obtenerPorId` vs `obtenerPorIdPublico`, cada servicio mantiene `obtenerPorId(id)` sin filtrar (lo siguen usando `actualizar`/`eliminar` internamente, que ya validan dueño después) y agrega un `obtenerPorIdVisible(id, solicitante)` nuevo, que es el que usa el controlador para lectura.

## 2. "Mis invitaciones" para el colaborador

Nuevo: `GET /api/colaboradores/mias` — todas las invitaciones a colaborar dirigidas al usuario autenticado, en cualquier estado. Acepta un filtro opcional `?estado=PENDIENTE` para ver solo lo que falta responder (el caso de uso que motivó este ítem: "qué me están proponiendo ahora mismo").

```
GET /api/colaboradores/mias?estado=PENDIENTE
```

## 3. "Mis ventas" para el productor

Nuevo: `GET /api/compras/mis-ventas` — el lado "venta" de la transacción: compras de licencias de los beats del propio productor, paginado y ordenado por fecha igual que `GET /api/compras` (que sigue siendo el lado "compra"). Requirió una consulta nueva en `CompraRepository` que atraviesa la cadena `Compra → TipoLicencia → Beat → productor`, ya que `Compra` no referencia al productor directamente.

## Archivos tocados — resumen completo

**Nuevos:**
- `src/test/java/.../security/AutorizacionUtilTest.java`

**Modificados:**
- `security/AutorizacionUtil.java` (nuevo `esDuenioOColaborador`)
- `service/TipoLicenciaService.java` (`obtenerTodosVisibles`, `obtenerPorIdVisible`)
- `service/ColaboradorBeatService.java` (`obtenerTodosVisibles`, `obtenerPorIdVisible`, `obtenerMisInvitaciones`)
- `service/AcuerdoCreditosService.java` (`obtenerTodosVisibles`, `obtenerPorIdVisible`)
- `service/BeatService.java` (`obtenerHistorialCreditos` refactorizado para reusar el helper compartido)
- `service/CompraService.java` (`obtenerMisVentas`)
- `repository/ColaboradorBeatRepository.java` (`findByUsuarioId`)
- `repository/CompraRepository.java` (`findByProductorId`)
- `controller/TipoLicenciaController.java`, `ColaboradorBeatController.java`, `AcuerdoCreditosController.java`, `CompraController.java`
- Tests de servicio correspondientes (visibilidad, mis-invitaciones, mis-ventas)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 111 tests, 110 pasan
```

Sigue fallando solo `LicenseApplicationTests.contextLoads()` (Postgres local), no relacionado con este bloque. No hizo falta ninguna migración de Flyway nueva — estos cambios son solo de lógica de visibilidad en la capa de servicio, no tocan el esquema de la base de datos.

## Con esto, el backend queda sin pendientes conocidos

Repaso final: los 7 ítems 🔴 imprescindibles (menos el frontend, que sigue aparte), los 4 🟠 recomendados, los 4 🟢 opcionales, las 2 decisiones de diseño, y ahora estos 3 huecos de visibilidad — todo implementado, testeado y documentado bloque por bloque en `docs/`.

## Sugerencia de mensaje de commit

```
fix(backend): cierra huecos de visibilidad en licencias/colaboradores/acuerdos
    + agrega mis-invitaciones y mis-ventas

- TipoLicencia solo visible si el beat esta publicado o sos su productor
- ColaboradorBeat y AcuerdoCreditos solo visibles para el productor dueño
  del beat o sus colaboradores (antes cualquier autenticado veia el split
  de creditos de cualquier beat)
- nuevo AutorizacionUtil.esDuenioOColaborador, reutilizado tambien en
  BeatService.obtenerHistorialCreditos (antes tenia el chequeo duplicado)
- nuevo GET /api/colaboradores/mias (filtro opcional ?estado=)
- nuevo GET /api/compras/mis-ventas (lado venta, para el productor)
```
