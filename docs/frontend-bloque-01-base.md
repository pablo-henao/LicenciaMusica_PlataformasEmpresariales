# Frontend Bloque 1 — Base del proyecto (Tailwind, router, auth, layout)

Primer bloque del frontend, que hasta ahora era el scaffold por defecto de Vite. Este bloque no tiene pantallas de negocio todavía (catálogo, beats, compras) — es la base sobre la que se van a montar los bloques siguientes: cliente API, autenticación, layout y las dos pantallas de auth.

## Decisiones de stack (confirmadas contigo antes de arrancar)

- **Tailwind CSS v4** vía `@tailwindcss/vite` (plugin oficial, sin PostCSS manual).
- **React Router** para el ruteo — única opción real para una SPA de este tamaño.
- **Cliente HTTP propio** sobre `fetch` (`src/api/client.ts`) en vez de axios — no hacía falta una librería para adjuntar un header y parsear JSON.
- **Context + `localStorage`** para la sesión — no se justifica Redux/Zustand para el tamaño de esta app.
- El reproductor de audio (bloque siguiente) va a usar `Beat.urlPreview` tal cual — el backend no tiene subida de archivos, es un campo de texto con una URL.

## Toque de backend necesario: `GET /api/auth/me`

El JWT que emite el backend solo guarda el email como *subject* (`JwtService.generarToken(email)`). Sin un endpoint que resuelva "quién soy" a partir del token, el frontend no tenía forma de recuperar `id`/`nombre`/`rol` después de loguearse ni de mantener la sesión al refrescar la página (sin tener que guardar el email en texto plano y reusar `/api/usuarios/buscar` para un propósito que no es el suyo).

Se agregó:
```java
@GetMapping("/me")
public UsuarioResponse me(@AuthenticationPrincipal Usuario usuario) {
    return UsuarioResponse.desde(usuario);
}
```
en `AuthController`. Como `SecurityConfig` tenía `.requestMatchers("/api/auth/**").permitAll()` (pensado solo para `/register` y `/login`), ese comodín también hubiera dejado `/me` sin autenticación — se corrigió a `.requestMatchers("/api/auth/register", "/api/auth/login").permitAll()`, explícito, para que `/me` caiga bajo `anyRequest().authenticated()` como corresponde.

Verificado con `./gradlew compileJava` y la suite completa (111 tests, 110 pasan — el mismo `contextLoads()` de siempre, no relacionado).

## Estructura nueva del frontend

```
src/
  api/
    client.ts        — fetch wrapper: adjunta JWT, parsea ApiError, descarga de archivos binarios
    types.ts          — tipos que reflejan los DTOs del backend (se completa bloque a bloque)
  context/
    authContextDefinition.ts  — el objeto Context (separado por una regla de ESLint de Fast Refresh)
    AuthContext.tsx            — AuthProvider: login/registrar/logout, persiste el token, resuelve /me
    useAuth.ts                  — hook de consumo
  components/
    layout/
      Layout.tsx       — shell con Navbar + <Outlet/>
      Navbar.tsx        — marca, estado de sesión, botones de auth
    ProtectedRoute.tsx  — redirige a /login si no hay sesión; soporta restringir por rol
    FormAlert.tsx        — muestra errores de la API (mensaje + detalles de validación) en cualquier formulario
  pages/
    HomePage.tsx          — landing (la reemplaza el catálogo en el Bloque 2)
    auth/
      LoginPage.tsx
      RegisterPage.tsx
  App.tsx   — rutas
```

**Por qué `authContextDefinition.ts` está separado del `AuthContext.tsx`:** la regla de ESLint `react-refresh/only-export-components` (ya configurada en el proyecto) no deja que un archivo exporte un Context/hook junto a un componente — rompe el Fast Refresh de Vite. Terminó en 3 archivos en vez de 1: el Context puro, el Provider, y el hook `useAuth`. Nota aparte: en Windows, `AuthContext.tsx` y un archivo `authContext.ts` (mismo nombre, minúscula inicial) chocan porque el filesystem no distingue mayúsculas — por eso el nombre quedó `authContextDefinition.ts` en vez de `authContext.ts`.

## Qué se puede hacer ya

- Registrarse (elige rol productor/comprador — admin no es seleccionable, como corresponde).
- Iniciar sesión.
- Cerrar sesión.
- La sesión persiste al refrescar la página (el token vive en `localStorage`, y al cargar la app se valida contra `/api/auth/me`).
- Errores del backend (validación, credenciales incorrectas, email duplicado) se muestran en el propio formulario, no como una alerta genérica.

## Verificación hecha

- `npm run build` (TypeScript + Vite) — limpio.
- `npm run lint` (ESLint) — limpio.
- Probado en el navegador (Vite dev server en `:5173`, con un proxy a `:8080` para `/api/**` configurado en `vite.config.ts` — así el frontend llama a rutas relativas `/api/...` sin preocuparse por CORS en desarrollo):
  - La landing, el toggle de rol en el registro, y el login renderizan y se ven bien en desktop y en mobile (375px).
  - Se completó el formulario de registro y se envió: el proxy reenvió correctamente la petición a `:8080/api/auth/register`, y como el backend no estaba corriendo, la respuesta 502 se mostró como un error legible en el formulario (confirma que el manejo de errores funciona, incluso para fallas de red, no solo errores 4xx del backend).

⚠️ **Lo que no pude probar:** el flujo completo de registro/login contra un backend real con Postgres funcionando — igual que con Flyway, no tengo tus credenciales de base de datos. Te recomiendo, antes de seguir con el Bloque 2:
```bash
DB_PASSWORD=<tu password> ./gradlew bootRun   # en backend/
npm run dev                                    # en frontend/
```
y probar registrarte de verdad — así confirmamos que `/api/auth/me` funciona contra datos reales antes de construir el resto de la app sobre esa base.

## Archivos tocados — resumen completo

**Backend:**
- `controller/AuthController.java` (`GET /me`)
- `config/SecurityConfig.java` (permitAll acotado a `/register` y `/login`)

**Frontend — nuevos:**
- `src/api/client.ts`, `src/api/types.ts`
- `src/context/authContextDefinition.ts`, `AuthContext.tsx`, `useAuth.ts`
- `src/components/layout/Layout.tsx`, `Navbar.tsx`
- `src/components/ProtectedRoute.tsx`, `FormAlert.tsx`
- `src/pages/HomePage.tsx`, `pages/auth/LoginPage.tsx`, `RegisterPage.tsx`
- `.claude/launch.json` (para poder previsualizar el frontend)

**Frontend — modificados:**
- `package.json` (`react-router-dom`, `tailwindcss`, `@tailwindcss/vite`)
- `vite.config.ts` (plugin de Tailwind, proxy de `/api` a `:8080`)
- `src/index.css` (Tailwind + paleta de marca, tomé el morado `#5B2C91` de la tabla del documento de presentación original)
- `src/App.tsx` (reescrito: rutas)
- `index.html` (título)

**Frontend — eliminados** (cruft del scaffold de Vite, ya no se usaban):
- `src/App.css`
- `src/assets/react.svg`, `vite.svg`, `hero.png`
- `public/icons.svg`

## Qué sigue

**Bloque 2 — Catálogo público + detalle de beat + reproductor**, reemplazando el placeholder de `HomePage`.

## Sugerencia de mensaje de commit

```
feat(frontend): base del proyecto — Tailwind, router, autenticacion y layout

- Tailwind CSS v4 (@tailwindcss/vite), React Router, cliente fetch propio
- AuthContext con login/registro/logout, sesion persistida via localStorage
  y validada contra el nuevo GET /api/auth/me
- Layout + Navbar consciente del estado de sesion, ProtectedRoute reutilizable
- paginas de login y registro con manejo de errores de validacion del backend
- limpia el scaffold por defecto de Vite (App.css, assets sin usar)

backend: agrega GET /api/auth/me (necesario para que el frontend resuelva el
usuario autenticado a partir del JWT) y acota el permitAll de SecurityConfig
a /register y /login especificamente, no a todo /api/auth/**
```
