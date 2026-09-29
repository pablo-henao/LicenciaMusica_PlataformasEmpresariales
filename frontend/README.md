# Licencia+ — Frontend

SPA en React + TypeScript + Vite que consume la API REST del backend (Spring Boot). No hay datos hardcodeados ni mocks: todo el catálogo, la autenticación y los formularios se conectan en tiempo real contra el backend.

## Stack

React 19 · TypeScript · Vite · React Router · Tailwind CSS v4 · fetch nativo (sin Axios).

## 1. Instalar dependencias

```bash
npm install
```

## 2. Configurar la URL del backend

En desarrollo no hace falta ningún `.env`: el proxy de Vite (`vite.config.ts`) reenvía las rutas relativas `/api/...` a `http://localhost:8080`, así que solo el backend debe estar corriendo.

Para apuntar a un backend en otro host (por ejemplo, un `dist/` de producción servido aparte), copia `.env.example` a `.env` y define:

```bash
VITE_API_URL=https://tu-backend
```

(sin `/` final). Ver `.env.example` en la raíz de esta carpeta.

## 3. Ejecutar el proyecto

```bash
npm run dev
```

Abre `http://localhost:5173`. El backend debe estar corriendo en `http://localhost:8080` (ver el README de `backend/` en la raíz del repositorio) — si no, el login y todas las pantallas muestran "El servidor no responde" en vez de fallar en silencio.

## Otros comandos

```bash
npm run build   # tsc -b && vite build — compila y empaqueta a dist/
npm run lint    # eslint .
npm run preview # sirve dist/ localmente para probar el build de producción
```

## Estructura

```
src/
├── api/          # Un módulo por recurso del backend (beats.ts, compras.ts, ...).
│                 # Toda llamada HTTP vive acá — los componentes nunca llaman fetch directo.
├── components/   # UI reutilizable, agrupada por dominio (beats/, colaboradores/, layout/).
├── context/      # AuthContext: token JWT, usuario autenticado, login/registro/logout.
├── hooks/        # useApiFetch: patrón estándar de "cargar datos al montar".
├── pages/        # Una carpeta por sección (auth/, catalogo/, beats/, compras/, admin/, ...).
└── utils/        # Formateo de precios, fechas y etiquetas de los enums del backend.
```

## Autenticación

El login (`POST /api/auth/login`) devuelve un JWT que se guarda en `localStorage` (`src/api/client.ts`). Un único punto centralizado (`request()` en ese mismo archivo, equivalente a un interceptor de Axios) adjunta `Authorization: Bearer <token>` a cada petición. Al recargar la página, `AuthContext` restaura la sesión llamando a `GET /api/auth/me` si hay token guardado. Un 401 limpia el token automáticamente; "Cerrar sesión" en la barra de navegación lo hace de forma explícita y redirige a `/login`.

## Flujo de prueba end-to-end

Con el backend corriendo y los datos demo cargados (`docs/seed-demo.sql` en la raíz del repo, clave `demo1234` para todos):

1. Entra como productor (`djkalu@demo.com`) → **Mis beats** → crea uno nuevo o gestiona uno existente: define licencias, invita a un colaborador por email, abre el acuerdo de créditos.
2. Entra como el colaborador invitado (`yani@demo.com`) → **Mis splits** → acepta su porcentaje.
3. Como productor, publica el beat una vez el split suma 100% y todos aceptaron.
4. Entra como comprador (`mcsueno@demo.com`) → **Tienda** → abre el beat → **Comprar**: esto crea la compra en estado `PENDIENTE`.
5. En **Mis compras**, pulsa **Completar compra** para el checkout (genera el contrato en PDF) y descárgalo.
6. Como productor, revisa **Ventas**; como `admin@demo.com`, revisa **Admin** (vista de solo lectura de beats, compras y acuerdos).
