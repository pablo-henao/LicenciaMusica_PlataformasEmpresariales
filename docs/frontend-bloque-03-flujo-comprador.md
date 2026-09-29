# Frontend Bloque 3 — Flujo comprador (comprar, checkout, historial, contrato)

## Qué se construyó

- **Botón "Comprar"** en cada licencia del detalle del beat (`BeatDetailPage`, ya existía desde el Bloque 2, ahora queda completo).
- **`GET /mis-compras`** (nueva, protegida): historial paginado, con badge de estado y la acción que corresponde según el estado.

## El flujo respeta las dos etapas que ya existían en el backend

El backend (Bloque 4 del backend) modela la compra en dos pasos deliberados: `crear` (queda `PENDIENTE`) y `checkout` (pasa a `COMPLETADA` y recién ahí se genera el contrato en PDF) — documentado en su momento como "no hay pasarela de pago real; `checkout()` modela el paso de confirmar/completar la compra". El frontend respeta esa separación en vez de esconderla:

1. En el detalle del beat, **"Comprar"** solo llama a `POST /api/compras` (crea la fila `PENDIENTE`). Al terminar, el botón cambia a un link "Ir a mis compras →".
2. En **Mis compras**, cada fila `PENDIENTE` tiene un botón **"Completar compra"** que llama al `checkout`. Al completarse, la fila pasa a `COMPLETADA` y aparece el botón **"Descargar contrato"**.

Se evaluó fusionar ambos pasos en un solo clic (ya que no hay pago real de por medio), pero el backend construyó ese segundo paso a propósito como una acción explícita — esconderlo hubiera sido más "mágico" pero menos fiel al modelo que ya se diseñó y documentó.

## Descarga del contrato

El endpoint `GET /api/compras/{id}/contrato` requiere el header `Authorization` (no es un archivo público), así que un `<a href>` normal no sirve — el navegador no manda el JWT en una navegación de link. Se resolvió en `descargarArchivo()` (agregado a `src/api/client.ts` en el Bloque 1, ya estaba listo para esto): pide el PDF con `fetch` + el header, arma un blob, y dispara la descarga con un `<a>` temporal generado por JS.

## Archivos nuevos

- `src/api/compras.ts` — `crearCompra`, `obtenerMisCompras`, `completarCompra`, `descargarContrato`.
- `src/components/Badge.tsx` — pill de estado reusable (pendiente/completada acá; se va a repetir para colaboradores, acuerdos, etc.).
- `src/pages/compras/MisComprasPage.tsx`.

## Archivos modificados

- `src/api/types.ts` — `CompraResponse`, `EstadoCompra`.
- `src/pages/catalogo/BeatDetailPage.tsx` — botón "Comprar" por licencia, con su propio estado de carga/error.
- `src/App.tsx` — ruta `/mis-compras` (protegida).
- `src/components/layout/Navbar.tsx` — link "Mis compras".

## Cómo se refresca la lista después de una acción

`useApiFetch` (del Bloque 2) solo vuelve a pedir datos cuando cambian sus `deps`. Para que "Completar compra" actualice la fila sin recargar la página, `MisComprasPage` mantiene un contador `refrescar` que se incluye en las `deps` del hook y se incrementa después de un checkout exitoso — dispara un refetch limpio sin necesitar mutar el estado a mano.

## Verificación hecha

- `npm run build` y `npm run lint` — limpios.
- Confirmé en el navegador que `/mis-compras` sin sesión redirige a `/login`, igual que el resto de rutas protegidas.

⚠️ **Lo que no pude probar:** el flujo completo (comprar → completar → descargar el PDF) contra datos reales, por el mismo motivo de siempre — no tengo tu Postgres. La UI reutiliza `Badge`, `FormAlert`, `Pagination` y los mismos estilos de botón ya verificados visualmente en bloques anteriores, así que el riesgo visual es bajo, pero antes de seguir te recomiendo probar el flujo una vez con datos reales:
```bash
DB_PASSWORD=<tu password> ./gradlew bootRun   # backend/
npm run dev                                    # frontend/
```
loguéate, comprá una licencia de algún beat publicado, completala en "Mis compras", y confirmá que el PDF se descarga bien.

## Qué sigue

**Bloque 4 — Flujo productor**: "Mis beats" (incluye borradores), crear/editar beat, publicar.

## Sugerencia de mensaje de commit

```
feat(frontend): flujo comprador completo — comprar, checkout, historial, contrato

- boton "Comprar" en el detalle del beat (POST /api/compras, dos pasos a proposito,
  respeta el diseño PENDIENTE/COMPLETADA que ya tenia el backend)
- nueva pagina /mis-compras: historial paginado, completar compra pendiente,
  descargar el contrato en PDF (via fetch+blob, el endpoint requiere JWT)
- nuevo Badge reusable para estados
```
