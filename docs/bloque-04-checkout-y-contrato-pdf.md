# Bloque 4 — Checkout + contrato en PDF (🔴 imprescindible, parte 4 de 5)

## Objetivo de este bloque

El enunciado (4.1) pide: *"Checkout con generación automática de contrato en PDF con los términos aceptados."* Antes de este bloque, `CompraService.crear()` solo guardaba una fila `PENDIENTE` — no había forma de completarla, no se generaba ningún documento, y de hecho no había ninguna validación de que el beat que se quería comprar estuviera publicado.

## Librería elegida: OpenPDF

Se agregó `com.github.librepdf:openpdf:3.0.5` a `build.gradle`. Se prefirió sobre Apache PDFBox porque su API de alto nivel (`Document`/`Paragraph`/`Font`) es la que mejor encaja para armar un documento tipo "contrato" con secciones y párrafos, sin tener que posicionar texto manualmente con coordenadas como exige PDFBox. Es la sucesora libre de iText (licencia LGPL/MPL).

⚠️ Detalle no documentado en los tutoriales más viejos de esta librería: en la versión 3.x el paquete raíz cambió de `com.lowagie.text` (heredado de iText) a **`org.openpdf.text`**. Si buscas ejemplos en internet basados en `com.lowagie`, van a compilar mal con esta versión — usa `org.openpdf.text.*`.

## Flujo de checkout implementado

1. `POST /api/compras` (ya existía) ahora **valida que el beat de la licencia esté `PUBLICADO`** antes de crear la compra — no tiene sentido iniciar una compra de un beat que sigue en `BORRADOR`. Si no lo está, `409` con `IllegalStateException`.
2. `PATCH /api/compras/{id}/checkout` (nuevo, solo el comprador dueño) — transiciona `PENDIENTE → COMPLETADA` y genera el contrato en PDF con [`ContratoPdfGenerator`](../backend/src/main/java/music/license/pdf/ContratoPdfGenerator.java), guardando los bytes en la propia fila de `Compra` (columna nueva `contrato_pdf`).
3. `GET /api/compras/{id}/contrato` (nuevo, solo el comprador dueño) — descarga el PDF ya generado (`Content-Type: application/pdf`, `Content-Disposition: attachment`). Si la compra no está completada todavía, `409`.

No hay pasarela de pago real (no estaba en el stack tecnológico del enunciado ni se pidió); `checkout()` modela el paso de "confirmar/completar la compra" que la plataforma sí puede controlar. Lo dejo explícito para que no se lea como un descuido.

## Por qué el PDF se guarda en la fila, no se regenera al vuelo

El contrato queda **congelado** con los datos tal como estaban en el momento del checkout (precio, condiciones, título del beat, etc.), guardado como `byte[]` en la propia `Compra`. Si más adelante el productor cambia el precio o las condiciones de ese `TipoLicencia`, el contrato ya emitido no se altera — es lo que se espera de un contrato real. La alternativa (regenerar el PDF cada vez que se pide `/contrato` a partir del estado actual de la BD) rompería esa garantía.

Nota técnica: el campo `Compra.contratoPdf` se declaró **sin** `@Lob` a propósito. Con `@Lob`, algunos combos Hibernate/driver de Postgres mapean `byte[]` a un *large object* (OID), que se comporta distinto a una columna normal (requiere manejo especial, no es un simple `SELECT`). Sin `@Lob`, Hibernate mapea `byte[]` directo a `bytea`, que es una columna binaria normal — más que suficiente para un PDF de unos pocos KB, y sin sorpresas.

## Decisión resuelta del Bloque 2: ¿se puede borrar una compra completada?

En el Bloque 2 dejé esta pregunta pendiente "para el bloque de checkout". Ya con el checkout implementado, la respuesta es no: **`CompraService.eliminar` ahora rechaza borrar una compra `COMPLETADA`** (409, mismo criterio que ya se usó con `AcuerdoCreditos` cerrado en el Bloque 3) — una vez emitido el contrato, no tiene sentido dejar que el registro desaparezca. Compras `PENDIENTE` sí se pueden seguir cancelando/borrando libremente por su dueño.

## Contenido del contrato generado

Con los datos que ya existen en el modelo (sin inventar campos nuevos): partes (productor/comprador con nombre y email), obra licenciada (título, género, BPM), términos de la licencia (tipo, precio, condiciones), fecha de la transacción, y una cláusula estándar de aceptación. Ver [`ContratoPdfGenerator.java`](../backend/src/main/java/music/license/pdf/ContratoPdfGenerator.java) para el detalle exacto.

## `CompraResponse`: nuevo campo `contratoDisponible`

Booleano simple (`compra.getContratoPdf() != null`) para que el frontend sepa si mostrar el botón de "descargar contrato" sin tener que pedir el PDF completo solo para chequear si existe.

## Archivos tocados — resumen completo

**Nuevos:**
- `pdf/ContratoPdfGenerator.java`
- `src/test/java/.../pdf/ContratoPdfGeneratorTest.java` (test real, no mockeado: genera un PDF y valida la firma `%PDF-`)

**Modificados:**
- `build.gradle` (dependencia `openpdf`)
- `model/Compra.java` (campo `contratoPdf`)
- `service/CompraService.java` (`crear` valida beat publicado; nuevo `checkout`, `obtenerContrato`; `eliminar` bloquea compras completadas)
- `controller/CompraController.java` (`PATCH /checkout`, `GET /contrato`)
- `dto/compra/CompraResponse.java` (campo `contratoDisponible`)
- `src/test/java/.../CompraServiceTest.java` (reescrito con el flujo completo)

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 69 tests, 68 pasan
```

El PDF generado se verificó automáticamente (no solo "compila"): el test `ContratoPdfGeneratorTest` genera un contrato real con datos de ejemplo y confirma que el archivo resultante no está vacío y empieza con la firma binaria `%PDF-` que identifica a cualquier PDF válido. Sigue fallando solo `LicenseApplicationTests.contextLoads()` por el mismo motivo de siempre (Postgres local) — no relacionado con este bloque.

## Qué falta (próximo bloque y pendientes acumulados)

- **Bloque 5 — Catálogo con filtros (género/BPM) + historial de compras** con paginación real. `GET /api/compras` ya devuelve "mis compras" (Bloque 2) y ahora trae `contratoDisponible`; falta la parte de filtros del catálogo de beats.
- **Pendientes del Bloque 2** (sin tocar, como pediste): si cerramos `GET /api/usuarios`.
- **Pendiente del Bloque 3**: entidad de historial/auditoría formal del acuerdo de créditos (🟠).
- Como siempre: nada de frontend todavía — sigue siendo el bloque más grande pendiente en general.

## Sugerencia de mensaje de commit

```
feat(backend): checkout de compras con generacion automatica de contrato en PDF

- nueva dependencia openpdf para generar el contrato (partes, obra, terminos, fecha)
- Compra.crear valida que el beat este publicado antes de permitir la compra
- nuevo endpoint PATCH /api/compras/{id}/checkout: PENDIENTE -> COMPLETADA + genera
  y guarda el PDF (congelado con los datos del momento del checkout)
- nuevo endpoint GET /api/compras/{id}/contrato para descargar el PDF ya emitido
- una compra COMPLETADA ya no se puede eliminar (el contrato ya fue emitido)
- test real (no mockeado) que valida que el PDF generado es un archivo valido
```
