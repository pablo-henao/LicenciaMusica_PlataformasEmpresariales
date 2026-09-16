# Bloque 5 — Catálogo con filtros + historial paginado (🔴 imprescindible, parte 5 de 5)

## Objetivo de este bloque

Último bloque 🔴 del backend. El enunciado (4.1) pide *"catálogo de beats... con filtro por género/BPM"* e *"historial de compras y licencias adquiridas para el comprador"*. Antes de este bloque, `GET /api/beats` devolvía **todos** los beats de **todos** los productores sin filtrar — incluidos los que seguían en `BORRADOR` — y sin paginar; `GET /api/compras` (desde el Bloque 2) ya devolvía solo las compras del usuario pero también sin paginar ni ordenar.

## Catálogo: qué cambió

`GET /api/beats` ahora:
1. **Solo devuelve beats `PUBLICADO`** — antes cualquier usuario autenticado veía los borradores de cualquier productor, lo cual no tiene sentido para un catálogo de venta.
2. Acepta filtros opcionales por querystring: `?genero=Reggaeton&bpmMin=90&bpmMax=100` (género es comparación exacta case-insensitive; bpmMin/bpmMax arman un rango, cualquiera de los dos es opcional).
3. Está **paginado** (`?page=0&size=20`, con `size=20` por defecto) usando `Pageable`/`Page` de Spring Data.

Como filtrar por `PUBLICADO` deja fuera los borradores de todos, un productor necesitaba una forma de ver (y gestionar) sus propios beats sin publicar. Por eso se agregó:

- **`GET /api/beats/mios`** (nuevo, requiere estar autenticado, sin restricción de rol): devuelve los beats del usuario autenticado sin filtrar por estado, paginado igual que el catálogo.

Y para que la ocultación de borradores sea consistente en todos lados, no solo en el listado:

- **`GET /api/beats/{id}`** ahora también oculta un beat `BORRADOR` a cualquiera que no sea su dueño — responde `404` igual que si no existiera, en vez de `200` con los datos del borrador. La razón de tratarlo como "no encontrado" y no como "prohibido" (`403`) es no confirmarle a un desconocido que existe un borrador con ese id. Esto es una regla de **visibilidad**, separada de la regla de **autorización para editar/borrar** que ya existía desde el Bloque 2 (esa se mantiene igual: 403 si no eres el dueño de un beat que sí puedes ver).

**Nota de implementación:** por eso hay dos métodos en `BeatService` — `obtenerPorId(id)` (interno, sin gating, lo siguen usando `actualizar`/`eliminar`/`publicar` que de todos modos exigen dueño después) y `obtenerPorIdPublico(id, solicitante)` (el que usa el controlador para la vista de detalle). No se fusionaron para no cambiar el código de 403 a 404 en los tests de autorización del Bloque 2, que ya estaban bien como estaban.

## Historial de compras: qué cambió

`GET /api/compras` ahora pagina (`size=20` por defecto) y viene **ordenado por fecha descendente** (las compras más recientes primero) — antes devolvía todo sin orden garantizado. `CompraResponse` ya traía `contratoDisponible` desde el Bloque 4, así que el historial ya indica de una vez cuáles compras tienen contrato para descargar.

## Cómo se armó el filtro sin agregar una librería nueva

Se evaluó `JpaSpecificationExecutor` (Specifications dinámicas) pero para dos filtros opcionales simples era más código y una abstracción nueva sin necesidad. Se usó una sola consulta `@Query` en JPQL con el patrón `(:parametro IS NULL OR condicion)`, que deja pasar el filtro cuando el parámetro no vino en la request:

```java
@Query("""
        SELECT b FROM Beat b
        WHERE b.estado = :estado
        AND (:genero IS NULL OR LOWER(b.genero) = LOWER(:genero))
        AND (:bpmMin IS NULL OR b.bpm >= :bpmMin)
        AND (:bpmMax IS NULL OR b.bpm <= :bpmMax)
        """)
Page<Beat> buscarCatalogo(...);
```

## `PaginaResponse<T>`: por qué no se devuelve `Page<T>` directo

Serializar `org.springframework.data.domain.Page` directo en una respuesta REST es una práctica desaconsejada por el propio equipo de Spring (el formato interno de `Page` no es un contrato estable entre versiones, y trae metadata de Spring que no debería filtrarse a la API pública). Se creó un DTO genérico chico, [`PaginaResponse<T>`](../backend/src/main/java/music/license/dto/common/PaginaResponse.java) (`contenido`, `pagina`, `tamano`, `totalElementos`, `totalPaginas`), reutilizado tanto para el catálogo de beats como para el historial de compras — justifica la abstracción al usarse en más de un sitio con la misma forma.

## Archivos tocados — resumen completo

**Nuevo:**
- `dto/common/PaginaResponse.java`

**Modificados:**
- `repository/BeatRepository.java` (`buscarCatalogo` con filtros + paginación, `findByProductorId` paginado)
- `repository/CompraRepository.java` (`findByCompradorId` ahora paginado, reemplaza la versión con `List`)
- `service/BeatService.java` (`buscarCatalogo`, `obtenerMios`, `obtenerPorIdPublico`; se quitó `obtenerTodos()` sin filtro, que quedaba sin uso)
- `service/CompraService.java` (`obtenerTodas` ahora paginado)
- `controller/BeatController.java` (`GET /api/beats` paginado+filtrado, nuevo `GET /api/beats/mios`, `GET /{id}` oculta borradores ajenos)
- `controller/CompraController.java` (`GET /api/compras` paginado y ordenado por fecha)
- `src/test/java/.../BeatServiceTest.java`, `src/test/java/.../CompraServiceTest.java` (tests nuevos para catálogo, mis-beats, visibilidad de borradores, historial paginado)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 73 tests, 72 pasan
```

Sigue fallando solo `LicenseApplicationTests.contextLoads()` (Postgres local), no relacionado con este bloque.

⚠️ **Límite real de esta verificación:** el filtro `buscarCatalogo` es una consulta JPQL nueva que los tests unitarios (con Mockito) prueban mockeada, no contra una base de datos real — igual que el resto del proyecto, que no tiene tests de integración contra Postgres (el único que lo intenta, `contextLoads()`, no puede correr en este entorno). Te recomiendo probar manualmente `GET /api/beats?genero=Trap&bpmMin=90&bpmMax=150` contra tu Postgres local antes de dar este bloque por completamente validado — la sintaxis JPQL es estándar así que debería funcionar, pero no lo vi ejecutar contra datos reales.

## Con esto se cierra el backend 🔴 imprescindible. Pendientes acumulados

- **Del Bloque 2**: decidir si cerramos `GET /api/usuarios` (sigue expuesto a cualquier autenticado).
- **Del Bloque 3**: entidad de historial/auditoría formal del acuerdo de créditos (🟠, "quién propuso/aceptó/modificó").
- **De este bloque**: vista de "mis ventas" para el productor (compras de licencias de sus propios beats) — la dejé fuera porque no estaba en el alcance que definimos, pero es una extensión natural de lo que ya existe (`CompraRepository` necesitaría una query por `tipoLicencia.beat.productor.id`).
- **El más grande**: el frontend sigue siendo el scaffold por defecto de Vite — cero pantallas conectadas a esta API.

## Sugerencia de mensaje de commit

```
feat(backend): catalogo de beats con filtros/paginacion e historial de compras paginado

- GET /api/beats: solo beats publicados, filtro opcional genero/bpm, paginado
- nuevo GET /api/beats/mios: beats propios del productor, incluidos los borradores
- GET /api/beats/{id} oculta (404) un borrador que no es del solicitante
- GET /api/compras: paginado, ordenado por fecha descendente
- nuevo PaginaResponse<T> generico en vez de serializar Page<T> directo
```
