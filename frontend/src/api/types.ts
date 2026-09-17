// Tipos que reflejan los DTOs de respuesta del backend (music.license.dto.*).
// Se van completando a medida que cada bloque del frontend consume mas endpoints.

export type Rol = "PRODUCTOR" | "COMPRADOR" | "ADMIN";

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  rol: Rol;
}

export interface AuthResponse {
  token: string;
  tipo: string;
}

export interface PaginaResponse<T> {
  contenido: T[];
  pagina: number;
  tamano: number;
  totalElementos: number;
  totalPaginas: number;
}
