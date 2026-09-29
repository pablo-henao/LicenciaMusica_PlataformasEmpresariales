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

export type EstadoCompra = "PENDIENTE" | "COMPLETADA";

export interface CompraRequest {
  tipoLicenciaId: number;
}

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

export type RolColaborador = "PRODUCTOR" | "CO_PRODUCTOR" | "VOCALISTA" | "MEZCLA" | "MASTERING";
export type EstadoColaborador = "PENDIENTE" | "ACEPTADO" | "RECHAZADO";

export interface ColaboradorBeatResponse {
  id: number;
  beatId: number;
  beatTitulo: string;
  usuarioId: number;
  usuarioNombre: string;
  rol: RolColaborador;
  porcentajePropuesto: number;
  estado: EstadoColaborador;
}

export type EstadoAcuerdo = "ABIERTO" | "CERRADO";

export interface AcuerdoCreditosResponse {
  id: number;
  beatId: number;
  beatTitulo: string;
  estado: EstadoAcuerdo;
  fechaCierre: string | null;
}

export type TipoEventoAcuerdo =
  | "ABIERTO"
  | "PROPUESTA"
  | "MODIFICACION"
  | "ACEPTACION"
  | "RECHAZO"
  | "ELIMINACION"
  | "CIERRE"
  | "REAPERTURA";

export interface AcuerdoCreditosEventoResponse {
  id: number;
  tipo: TipoEventoAcuerdo;
  detalle: string | null;
  fecha: string;
  usuarioId: number;
  usuarioNombre: string;
}

export type TipoNotificacion =
  | "INVITACION_COLABORACION"
  | "ACEPTACION_COLABORACION"
  | "RECHAZO_COLABORACION"
  | "COMPRA_COMPLETADA";

export interface NotificacionResponse {
  id: number;
  tipo: TipoNotificacion;
  mensaje: string;
  leida: boolean;
  fecha: string;
}
