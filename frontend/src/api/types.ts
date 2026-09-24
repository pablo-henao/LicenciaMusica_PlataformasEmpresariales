// Tipos que reflejan los DTOs de respuesta del backend (music.license.dto.*).

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

export interface BeatRequest {
  titulo: string;
  genero: string;
  bpm: number;
  urlPreview?: string;
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

export interface TipoLicenciaRequest {
  beatId: number;
  tipo: TipoLicenciaEnum;
  precio: number;
  condiciones?: string;
}

export type EstadoCompra = "PENDIENTE" | "COMPLETADA";

export interface CompraResponse {
  id: number;
  compradorId: number;
  compradorNombre: string;
  tipoLicenciaId: number;
  beatTitulo: string;
  precio: number;
  fecha: string;
  estado: EstadoCompra;
  contratoDisponible: boolean;
}

export interface CompraRequest {
  tipoLicenciaId: number;
}

export type EstadoColaborador = "PENDIENTE" | "ACEPTADO" | "RECHAZADO";

export type RolColaborador =
  | "PRODUCTOR"
  | "CO_PRODUCTOR"
  | "VOCALISTA"
  | "MEZCLA"
  | "MASTERING";

export interface ColaboradorBeatResponse {
  id: number;
  beatId: number;
  usuarioId: number;
  usuarioNombre: string;
  rol: RolColaborador;
  porcentajePropuesto: number;
  estado: EstadoColaborador;
}

export interface ColaboradorBeatRequest {
  beatId: number;
  usuarioId: number;
  rol: RolColaborador;
  porcentajePropuesto: number;
}

export type EstadoAcuerdo = "ABIERTO" | "CERRADO";

export interface AcuerdoCreditosResponse {
  id: number;
  beatId: number;
  estado: EstadoAcuerdo;
  fechaCierre: string | null;
}

export interface AcuerdoCreditosRequest {
  beatId: number;
}

export interface AcuerdoCreditosEventoResponse {
  id: number;
  tipo:
    | "ABIERTO"
    | "PROPUESTA"
    | "MODIFICACION"
    | "ACEPTACION"
    | "RECHAZO"
    | "ELIMINACION"
    | "CIERRE"
    | "REAPERTURA";
  detalle: string;
  fecha: string;
  usuarioId: number | null;
  usuarioNombre: string | null;
}

export interface NotificacionResponse {
  id: number;
  tipo:
    | "INVITACION_COLABORACION"
    | "ACEPTACION_COLABORACION"
    | "RECHAZO_COLABORACION"
    | "COMPRA_COMPLETADA";
  mensaje: string;
  leida: boolean;
  fecha: string;
}
