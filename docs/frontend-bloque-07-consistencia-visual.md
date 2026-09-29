# Frontend Bloque 7 — Consistencia visual

Después de integrar el commit del colaborador (Bloque 6), la mitad de la app quedó con el lenguaje visual nuevo (tarjetas `rounded-2xl`, botones tipo píldora `rounded-full`, tipografía `font-bold tracking-tight`) y la otra mitad —las pantallas que no tocamos porque ya eran nuestras y funcionaban bien— seguía con el estilo plano original de los Bloques 1-5 (`rounded-md`/`rounded-lg`, `font-semibold`). Este bloque es puramente visual: unifica el lenguaje de diseño en toda la app, sin cambiar ninguna lógica ni llamada al backend.

## Qué se unificó

- **Componentes compartidos** (afectan toda la app de una vez): `FormAlert` (`rounded-xl`), `Pagination` (botones `rounded-full`).
- **Autenticación**: `LoginPage` y `RegisterPage` ahora envuelven el formulario en la misma tarjeta `rounded-2xl border border-neutral-200 bg-white p-6 sm:p-8` que usan las demás pantallas, con inputs `rounded-xl` y botón primario `rounded-full bg-neutral-900`.
- **Gestión de beats**: `BeatForm`, `MisBeatsPage`, `CrearBeatPage`, `EditarBeatPage`, `GestionarBeatPage` — mismo tratamiento de tarjetas e inputs, encabezados `font-bold tracking-tight`.
- **Licencias y colaboradores**: `LicenciaForm`, `LicenciasManager`, `FiltrosCatalogoForm`, `ColaboradoresManager`, `EditarColaboradorForm`, `InvitarColaboradorForm`, `HistorialCreditos` — inputs `rounded-xl`, botones de acción `rounded-full`, tarjetas `rounded-2xl`.
- `FiltrosCatalogoForm` además perdió su propio borde: ahora vive dentro de la tarjeta de `CatalogoPage`, que ya lo envuelve — antes quedaba una tarjeta dentro de otra tarjeta.

## Qué NO se tocó

Ninguna llamada a la API, ninguna validación, ninguna regla de negocio. Es estrictamente clases de Tailwind y jerarquía tipográfica.

## Verificación

- `npm run build` y `npm run lint` — limpios.
- Verificado en navegador: login y registro con el nuevo estilo, sin errores de consola.
- Confirmado que todas las páginas enlazadas desde la barra de navegación (Inicio, Tienda, Mis beats, Ventas, Compras, Mis splits, Avisos, Admin, y los anclas Licencias/Splits/Nosotros del home) existen y están ruteadas en `App.tsx`.
