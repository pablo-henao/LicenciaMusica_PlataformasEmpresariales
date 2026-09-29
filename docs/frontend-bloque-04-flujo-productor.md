# Frontend Bloque 4 — Flujo productor (mis beats, crear/editar, publicar)

## Qué se construyó

- **`GET /mis-beats`** (protegida, solo rol `PRODUCTOR`): lista paginada de los beats propios, **incluidos los borradores** (usa `GET /api/beats/mios` del backend, distinto del catálogo público que solo muestra publicados).
- **`GET /beats/nuevo`**: formulario de creación — el beat siempre nace en `BORRADOR`.
- **`GET /beats/:id/editar`**: mismo formulario, precargado con los datos actuales.
- **Publicar**: botón directo en la fila de "Mis beats" (no hace falta una pantalla aparte) que llama a `PATCH /api/beats/{id}/publicar`.

Las tres rutas nuevas usan `rolesPermitidos={["PRODUCTOR"]}` en `ProtectedRoute` (ya existía desde el Bloque 1, primera vez que se usa la restricción por rol) — un comprador que intente entrar por URL directa ve el mensaje de "no tiene permiso", no un error roto.

## Dónde termina este bloque y dónde empieza el siguiente

`MisBeatsPage` no tiene todavía un link a "gestionar licencias y colaboradores" de cada beat — ese es el contenido completo del Bloque 5. Se decidió no crear esa pantalla a medias ahora (un botón que lleve a una página vacía es peor que no tener el botón). Cuando el Bloque 5 construya esa vista, ahí se agrega el link desde esta lista.

## Por qué "Publicar" es un botón en la lista y no una pantalla aparte

Publicar es una transición de estado sin datos que pedirle al usuario (no es un formulario) — un botón con confirmación visual (cambia a badge "Publicado" al toque) alcanza. Si el backend rechaza la publicación (409, por ejemplo "el acuerdo de créditos debe estar cerrado" — la regla del Bloque 3 del backend), el mensaje de error del backend se muestra tal cual arriba de la lista; construir una UI que explique *por qué* no se puede publicar y guíe a resolverlo (cerrar el acuerdo de créditos) es del Bloque 5, cuando exista la pantalla de colaboradores/acuerdo.

## Formulario compartido

`BeatForm` (un solo componente) sirve para crear y editar — la única diferencia entre las dos pantallas es si vienen `valoresIniciales` y qué texto tiene el botón. Los límites del formulario (`bpm` entre 40 y 300, campos obligatorios) reflejan las validaciones de `BeatRequest` del backend (`@Min`/`@Max`/`@NotBlank`), como una primera capa de feedback — el backend sigue siendo quien valida de verdad.

## Archivos nuevos

- `src/components/beats/BeatForm.tsx`
- `src/pages/beats/MisBeatsPage.tsx`, `CrearBeatPage.tsx`, `EditarBeatPage.tsx`

## Archivos modificados

- `src/api/beats.ts` — `obtenerMisBeats`, `crearBeat`, `actualizarBeat`, `publicarBeat`.
- `src/App.tsx` — rutas `/mis-beats`, `/beats/nuevo`, `/beats/:id/editar`.
- `src/components/layout/Navbar.tsx` — link "Mis beats", visible solo para rol `PRODUCTOR`.

## Verificación hecha

- `npm run build` y `npm run lint` — limpios.
- Confirmé en el navegador que `/mis-beats` y `/beats/nuevo` sin sesión redirigen a `/login`.

⚠️ **Lo que no pude probar:** el flujo completo (crear → editar → publicar) contra datos reales — mismo motivo de siempre, sin tu Postgres. Te recomiendo probarlo una vez que tengas un momento:
```bash
DB_PASSWORD=<tu password> ./gradlew bootRun   # backend/
npm run dev                                    # frontend/
```
regístrate como productor, crea un beat, publícalo, y confirma que aparece en `/catalogo` para otro usuario.

## Qué sigue

**Bloque 5 — Gestión de licencias, colaboradores, acuerdo de créditos e historial**, dentro de una nueva vista de detalle del productor para cada beat propio (`/mis-beats/:id`), enlazada desde esta lista.

## Sugerencia de mensaje de commit

```
feat(frontend): flujo productor — mis beats, crear/editar, publicar

- GET /mis-beats (solo PRODUCTOR): lista paginada, incluye borradores
- formulario compartido (BeatForm) para crear y editar
- boton "Publicar" directo en la lista, con el error del backend visible
  si la publicacion se rechaza (ej. acuerdo de creditos no cerrado)
- primer uso de la restriccion por rol en ProtectedRoute
```
