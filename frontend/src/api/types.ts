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

export type EstadoBeat = "BORRADOR" | "PUBLICADO";

export interface BeatResponse {
  id: number;
  titulo: string;
  genero: string;
  bpm: number;
  urlPreview: string | null;
  estado: EstadoBeat;
  productorId: number;
  productorNombre: string;
}

export type TipoLicenciaEnum = "EXCLUSIVA" | "NO_EXCLUSIVA" | "COMERCIAL_LIMITADA";

export interface TipoLicenciaResponse {
  id: number;
  beatId: number;
  beatTitulo: string;
  tipo: TipoLicenciaEnum;
  precio: number;
  condiciones: string | null;
}
