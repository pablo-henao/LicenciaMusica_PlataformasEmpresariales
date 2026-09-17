# Frontend Bloque 2 — Catálogo público, detalle de beat y reproductor

## Qué se construyó

- **`GET /catalogo`** (protegida, cualquier rol autenticado): grilla de beats publicados, con filtro por título/género/BPM (mismos parámetros que `GET /api/beats` del backend) y paginación.
- **`GET /beats/:id`** (protegida): detalle del beat — género, BPM, reproductor (`<audio>` apuntando a `urlPreview`), y la lista de licencias disponibles con su precio y condiciones.
- El reproductor usa el campo `urlPreview` tal cual, como se había definido en el Bloque 1 — no hay subida de archivos en el backend.
- `HomePage` y la `Navbar` ahora enlazan al catálogo cuando hay sesión iniciada.

**A propósito NO incluye todavía:** el botón "Comprar". La página de detalle muestra las licencias como información (tipo, precio, condiciones), pero la acción de compra (crear la `Compra`, hacer checkout, descargar el contrato) es el Bloque 3 completo — mezclar ambas cosas hubiera dejado un botón a medio funcionar en este bloque.

## Un límite real del backend que se resolvió del lado del cliente

`GET /api/licencias` no tiene un filtro por `beatId` — devuelve **todas** las licencias visibles para el usuario autenticado, sin paginar. Para mostrar "las licencias de este beat" en la página de detalle, se pide la lista completa y se filtra en el cliente (`obtenerLicenciasDeBeat` en `src/api/licencias.ts`). Funciona bien para el tamaño de catálogo de este proyecto; si el catálogo creciera mucho, valdría la pena agregar el filtro al backend — lo dejo anotado, no lo cambié porque no hacía falta para que funcione correctamente ahora.

## Una regla de ESLint que se va a repetir en cada pantalla: `useApiFetch`

Las reglas nuevas de `eslint-plugin-react-hooks` (ya configuradas en el proyecto desde el scaffold) prohíben llamar a `setState` de forma sincrónica en el cuerpo de un efecto — el patrón típico de "pantalla que carga datos al montar" (`useEffect(() => { setCargando(true); fetch().then(...).finally(() => setCargando(false)) }, [])`) dispara el error apenas se le pone `setCargando(true)` como primera línea.

Como esto se iba a repetir en **todas** las pantallas que siguen (mis-beats, gestión de colaboradores, compras, notificaciones, panel admin...), en vez de parchear cada `useEffect` a mano se armó un hook compartido una sola vez: [`useApiFetch`](../frontend/src/hooks/useApiFetch.ts). Encadena todo con promesas (`Promise.resolve().then(() => setCargando(true))...`) en vez de un `setState` suelto, así todos los `setState` quedan dentro de callbacks de promesa — que es lo que la regla exige — y cada pantalla nueva solo tiene que llamarlo:

```ts
const { datos, cargando, error } = useApiFetch(fetcher, [dependencias], "mensaje de error");
```

`CatalogoPage` y `BeatDetailPage` ya lo usan; el resto de los bloques del frontend también lo va a usar.

## Archivos nuevos

- `src/hooks/useApiFetch.ts` — hook compartido de carga de datos (ver arriba).
- `src/utils/format.ts` — `formatearPrecio` (COP), `formatearFecha`, y las etiquetas en español de `TipoLicenciaEnum`. Se agregó `formatearFecha` ya de una vez aunque este bloque no lo use, porque el Bloque 3 (compras) lo va a necesitar y es una función pura sin riesgo de tener que rehacerla.
- `src/api/beats.ts`, `src/api/licencias.ts` — llamadas al backend.
- `src/components/Pagination.tsx` — paginación reusable (se va a repetir en mis-beats, mis-compras, mis-ventas, admin).
- `src/components/beats/BeatCard.tsx`, `FiltrosCatalogoForm.tsx`.
- `src/pages/catalogo/CatalogoPage.tsx`, `BeatDetailPage.tsx`.

## Archivos modificados

- `src/api/types.ts` — `BeatResponse`, `TipoLicenciaResponse`, `EstadoBeat`, `TipoLicenciaEnum`.
- `src/App.tsx` — rutas `/catalogo` y `/beats/:id`, ambas protegidas.
- `src/components/layout/Navbar.tsx` — link "Catálogo" (solo visible con sesión iniciada).
- `src/pages/HomePage.tsx` — el CTA de usuario logueado ahora enlaza al catálogo real.

## Verificación hecha

- `npm run build` y `npm run lint` — limpios.
- Confirmé en el navegador que `/catalogo` sin sesión iniciada redirige correctamente a `/login` (el `ProtectedRoute` del Bloque 1 funciona igual para las rutas nuevas).

⚠️ **Lo que no pude probar:** el catálogo con datos reales (necesita tu Postgres corriendo con al menos un beat publicado). La UI reutiliza los mismos componentes de formulario/tarjeta ya verificados visualmente en el Bloque 1, así que el riesgo visual es bajo, pero te recomiendo, cuando tengas un momento:
```bash
DB_PASSWORD=<tu password> ./gradlew bootRun   # backend/
npm run dev                                    # frontend/
```
loguearte, y si no hay beats todavía, crear uno y publicarlo a mano contra la API (con Swagger en `/swagger-ui.html`, o con curl) para ver el catálogo poblado — la pantalla de "crear beat" recién llega en el Bloque 4.

## Qué sigue

**Bloque 3 — Flujo comprador**: el botón "Comprar" en el detalle del beat, checkout, historial de compras (`/mis-compras`) y descarga del contrato en PDF.

## Sugerencia de mensaje de commit

```
feat(frontend): catalogo publico, detalle de beat y reproductor

- GET /catalogo: grilla filtrable (titulo/genero/bpm) y paginada de beats publicados
- GET /beats/:id: detalle con reproductor (<audio> sobre urlPreview) y licencias
  disponibles (sin boton de compra todavia, es el Bloque 3)
- nuevo hook useApiFetch: patron de carga de datos compartido, evita repetir el
  workaround de la regla de eslint react-hooks/set-state-in-effect en cada pantalla
- Pagination reusable
```
