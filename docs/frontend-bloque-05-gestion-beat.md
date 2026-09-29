# Frontend Bloque 5 — Licencias, colaboradores, acuerdo de créditos e historial

El bloque más grande del frontend hasta ahora: toda la gestión de un beat propio vive en una página nueva, **`/mis-beats/:id`** ("Gestionar"), enlazada desde "Mis beats" (Bloque 4).

## Qué se construyó

Dentro de `GestionarBeatPage`, tres secciones independientes, cada una con su propia carga de datos:

1. **Licencias** (`LicenciasManager` + `LicenciaForm`): crear, editar y eliminar tipos de licencia del beat (tipo, precio, condiciones).
2. **Colaboradores y créditos** (`ColaboradoresManager`): estado del acuerdo de créditos (sin abrir / abierto / cerrado, con la suma de porcentajes visible y en verde cuando da 100%), invitar un colaborador por email, editarlo o quitarlo.
3. **Historial de créditos** (`HistorialCreditos`): línea de tiempo de solo lectura con cada evento (`GET /api/beats/{id}/historial-creditos` del Bloque 6 del backend).

## Una decisión de diseño importante: esta página NO tiene botones de aceptar/rechazar

Es tentador pensar que la pantalla de "gestionar mi beat" debería mostrar el estado de cada colaborador con botones de aceptar/rechazar al lado. **No es así, y es a propósito**: el backend exige explícitamente que sea el propio colaborador invitado quien acepte o rechace (`AutorizacionUtil.exigirPropietario` compara contra `colaboradorBeat.usuario`, no contra el productor) — el productor que ve esta página nunca va a poder ejecutar esa acción sobre su propia invitación a otra persona. Aceptar/rechazar es exclusivamente de **"Mis invitaciones"**, la pantalla que arma el Bloque 6 usando `GET /api/colaboradores/mias`. Ponerlos acá hubiera sido un botón que siempre da 403.

## Dos límites del backend, mismo patrón de solución que el Bloque 2

`GET /api/colaboradores` y `GET /api/acuerdos-creditos` tampoco tienen filtro por `beatId` (igual que `GET /api/licencias` en su momento) — se resolvió igual: pedir todo lo visible y filtrar en el cliente. `AcuerdoCreditos` es 1:1 con `Beat`, así que en la práctica el filtro encuentra como mucho una coincidencia.

## Por qué la edición de un colaborador avisa que "reabre el acuerdo"

El backend (Bloque 3 del backend) implementa literalmente "cualquier cambio reabre el acuerdo y reinicia las aceptaciones" — invitar, editar o quitar a un colaborador resetea a `PENDIENTE` a **todos**, no solo al que cambió, y si el acuerdo ya estaba `CERRADO` lo reabre. `EditarColaboradorForm` avisa esto explícitamente en el propio formulario, para que no sea una sorpresa después.

## Archivos nuevos

- `src/api/usuarios.ts`, `colaboradores.ts`, `acuerdos.ts` — llamadas al backend.
- `src/components/beats/LicenciaForm.tsx`, `LicenciasManager.tsx`.
- `src/components/colaboradores/InvitarColaboradorForm.tsx`, `EditarColaboradorForm.tsx`, `ColaboradoresManager.tsx`, `HistorialCreditos.tsx`.
- `src/pages/beats/GestionarBeatPage.tsx`.

## Archivos modificados

- `src/api/types.ts` — `ColaboradorBeatResponse`, `AcuerdoCreditosResponse`, `AcuerdoCreditosEventoResponse`, y sus enums.
- `src/api/licencias.ts` — `crearLicencia`, `actualizarLicencia`, `eliminarLicencia`.
- `src/utils/format.ts` — etiquetas y listas de opciones para rol de colaborador, estado de colaborador, tipo de evento del historial.
- `src/components/Badge.tsx` — exporta el tipo `BadgeVariante` (lo necesitaba `format.ts` para mapear estado → color sin duplicar la lista de variantes).
- `src/App.tsx` — ruta `/mis-beats/:id`.
- `src/pages/beats/MisBeatsPage.tsx` — link "Gestionar" por beat.

## Verificación hecha

- `npm run build` y `npm run lint` — limpios **a la primera**, sin ningún error de `react-hooks/set-state-in-effect` a pesar de que este bloque agregó 5 pantallas/secciones nuevas que cargan datos — confirma que el hook `useApiFetch` del Bloque 2 cumplió su propósito de evitar repetir ese problema.
- Confirmé en el navegador que `/mis-beats/:id` sin sesión redirige a `/login`.

⚠️ **Lo que no pude probar:** el flujo completo (abrir acuerdo → invitar → editar → ver el historial actualizarse) contra datos reales — mismo motivo de siempre. Es el bloque con más lógica de estado de todo el frontend hasta ahora, así que te recomiendo especialmente probarlo antes de seguir:
```bash
DB_PASSWORD=<tu password> ./gradlew bootRun   # backend/
npm run dev                                    # frontend/
```
Con dos cuentas (una productor, una para invitar como colaborador): crea un beat, ábrele el acuerdo, invita a la segunda cuenta, agrégale una licencia, y confirma que el historial va registrando cada paso.

## Qué sigue

**Bloque 6 — "Mis invitaciones"** (aceptar/rechazar, para el colaborador) y **"Mis ventas"** (para el productor).

## Sugerencia de mensaje de commit

```
feat(frontend): gestion de licencias, colaboradores, acuerdo de creditos e historial

- nueva pagina /mis-beats/:id con tres secciones: licencias (CRUD), colaboradores
  y acuerdo de creditos (invitar, editar, quitar, abrir acuerdo, suma de %),
  e historial de eventos de solo lectura
- a proposito sin botones de aceptar/rechazar: esa accion es exclusiva del
  colaborador invitado, no del productor (queda para el Bloque 6)
- licencias.ts, colaboradores.ts, acuerdos.ts: mismo patron de filtrado en el
  cliente que ya se uso en el Bloque 2 (el backend no filtra estos listados por beat)
```
