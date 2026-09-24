// Cliente HTTP chico sobre fetch: adjunta el JWT, arma la URL y traduce los
// errores del backend (ApiError: {status, error, mensaje, detalles}) a una
// excepcion de TypeScript que las pantallas puedan mostrar directo.

// Base de la API configurable para producción:
// - En desarrollo se deja VACÍA para usar rutas relativas ("/api/...") y que el
//   proxy de Vite (vite.config.ts) las reenvíe a http://localhost:8080 sin CORS.
// - En producción (dist/ servido en otro host) define VITE_API_URL, ej:
//   VITE_API_URL=https://api.mi-dominio.com  (sin "/" final)
// Ver frontend/.env.example
const API_BASE = (import.meta.env.VITE_API_URL as string | undefined)?.replace(/\/$/, "") ?? "";

export const MENSAJE_SERVIDOR_CAIDO =
  "El servidor no responde. Verifica que el backend esté corriendo en http://localhost:8080.";

const TOKEN_KEY = "licencia_token";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function limpiarToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

export class ApiError extends Error {
  status: number;
  detalles: string[];

  constructor(status: number, mensaje: string, detalles: string[] = []) {
    super(mensaje);
    this.name = "ApiError";
    this.status = status;
    this.detalles = detalles;
  }
}

interface ApiErrorBody {
  mensaje?: string;
  detalles?: string[];
}

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {};
  const token = getToken();

  if (options.body !== undefined) {
    headers["Content-Type"] = "application/json";
  }
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    method: options.method ?? "GET",
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  }).catch(() => {
    // fetch solo rechaza por fallos de red (backend apagado, DNS, CORS...):
    // se traduce a un mensaje accionable en vez del TypeError genérico.
    throw new ApiError(0, MENSAJE_SERVIDOR_CAIDO);
  });

  if (response.status === 204) {
    return undefined as T;
  }

  const tipoContenido = response.headers.get("content-type") ?? "";
  const esJson = tipoContenido.includes("application/json");
  const cuerpo = esJson ? ((await response.json()) as ApiErrorBody) : undefined;

  if (!response.ok) {
    if (response.status === 401) {
      limpiarToken();
    }

    throw new ApiError(
      response.status,
      cuerpo?.mensaje ?? "Ocurrió un error inesperado. Intenta de nuevo.",
      cuerpo?.detalles ?? [],
    );
  }

  return cuerpo as T;
}

function conQuery(path: string, params?: Record<string, string | number | boolean | undefined>): string {
  if (!params) {
    return path;
  }

  const query = new URLSearchParams();
  for (const [clave, valor] of Object.entries(params)) {
    if (valor !== undefined && valor !== "") {
      query.set(clave, String(valor));
    }
  }

  const texto = query.toString();
  return texto ? `${path}?${texto}` : path;
}

export const api = {
  get: <T>(path: string, params?: Record<string, string | number | boolean | undefined>) =>
    request<T>(conQuery(path, params)),
  post: <T>(path: string, body?: unknown) => request<T>(path, { method: "POST", body }),
  put: <T>(path: string, body?: unknown) => request<T>(path, { method: "PUT", body }),
  patch: <T>(path: string, body?: unknown) => request<T>(path, { method: "PATCH", body }),
  delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};

/**
 * Descarga un archivo binario (el contrato en PDF) autenticado con el JWT.
 * No se puede usar un <a href> plano porque el endpoint requiere el header
 * Authorization, que un link normal no puede mandar.
 */
export async function descargarArchivo(path: string, nombreArchivo: string): Promise<void> {
  const token = getToken();
  const headers: Record<string, string> = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, { headers }).catch(() => {
    throw new ApiError(0, MENSAJE_SERVIDOR_CAIDO);
  });

  if (!response.ok) {
    throw new ApiError(response.status, "No se pudo descargar el archivo");
  }

  const blob = await response.blob();
  const url = URL.createObjectURL(blob);

  const enlace = document.createElement("a");
  enlace.href = url;
  enlace.download = nombreArchivo;
  document.body.appendChild(enlace);
  enlace.click();
  document.body.removeChild(enlace);

  URL.revokeObjectURL(url);
}
