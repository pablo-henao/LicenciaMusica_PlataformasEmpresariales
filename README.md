# Licencia+ — Marketplace de beats con licencias y splits

Plataforma donde productores publican beats, venden licencias con contrato en PDF
y reparten créditos con colaboradores (split al 100% con aceptación de todos).

## Estructura

```
license/
├── backend/    Spring Boot 4 + JPA + Security (JWT) + PostgreSQL + Flyway + OpenPDF
└── frontend/   React + Vite + TypeScript + Tailwind CSS v4 + React Router
```

## Requisitos

* PostgreSQL local (puerto `5432`)
* JDK 25 (Gradle descarga el toolchain solo si te falta)
* Node 20+ y npm

## 1. Base de datos (primera vez)

Flyway (`backend/src/main/resources/db/migration/V1, V2`) es el dueño del esquema y
`ddl-auto=validate`: Hibernate **no crea** tablas. Si la base ya tiene tablas de una
corrida anterior, el arranque falla con `relation already exists` → recréala:

```sql
DROP DATABASE license_music;
CREATE DATABASE license_music;
```

## 2. Backend (`:8080`)

En `backend/`. La contraseña **no tiene default** y se pasa por entorno:

```powershell
# PowerShell
$env:DB_PASSWORD="tu_password"; .\gradlew.bat bootRun
```

```bash
# Git Bash / Linux / Mac
DB_PASSWORD=<tu-password> ./gradlew bootRun
```

Opcionales: `DB_URL` (default `jdbc:postgresql://localhost:5432/license_music`),
`DB_USERNAME` (default `postgres`), `JWT_SECRET`, `JWT_EXPIRATION`.

Verificación: `http://localhost:8080/swagger-ui.html` (Swagger) y
`GET /api/beats` responde `401` sin token (es lo esperado).

Tests: `DB_PASSWORD=<tu-password> ./gradlew test` (111 tests; solo falla el
`contextLoads()` si no hay Postgres local, no es regresión).

## 3. Frontend (`:5173`)

En otra terminal, en `frontend/`:

```bash
npm install   # solo la primera vez
npm run dev
```

Abrir `http://localhost:5173`. En desarrollo el front llama a rutas relativas
`/api/...` y Vite las proxifica a `http://localhost:8080` (ver `vite.config.ts`),
así que **el backend debe estar corriendo**; si no, verás
“El servidor no responde…” en vez de pantallas rotas.

Producción (`dist/` en otro host): copiar `.env.example` a `.env` y definir
`VITE_API_URL=https://tu-backend` (sin `/` final).

## 4. Datos demo (opcional, el catálogo no arranca vacío)

```bash
psql -h localhost -U postgres -d license_music -f docs/seed-demo.sql
```

Clave de todos: `demo1234`. Usuarios: `djkalu@demo.com` y `yani@demo.com`
(productores), `mcsueno@demo.com` (comprador), `admin@demo.com` (admin).
Incluye 3 beats publicados con licencias, 1 borrador con split pendiente
(para probar **Splits** como Yani) y 1 acuerdo cerrado con historial.

## 5. Flujo de prueba end-to-end

1. Entra como `djkalu@demo.com` → **Mis beats** → **Gestionar** un beat:
   licencias, invitar colaborador por email, abrir acuerdo.
2. Entra como `yani@demo.com` → **Splits** → acepta el 30% pendiente.
3. Como productor → **Publicar** (exige 100% + todos aceptados).
4. Como `mcsueno@demo.com` → **Tienda** → beat → **Comprar ahora** →
   descarga el **contrato PDF** → revisa **Compras** y **Avisos**.
5. Como productor → **Ventas**; como `admin@demo.com` → **Admin**.

## Diseño

Landing oscura cinematográfica (hero `LICENCIA+` gigante, stats, card de beat
destacado) inspirada en la referencia Winzy, 100% CSS sin imágenes externas.
El resto de la app conserva su tema claro.
