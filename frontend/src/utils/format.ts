import type {
  EstadoAcuerdo,
  EstadoBeat,
  EstadoColaborador,
  EstadoCompra,
  RolColaborador,
  TipoLicenciaEnum,
} from "../api/types";

const FORMATEADOR_PRECIO = new Intl.NumberFormat("es-CO", {
  style: "currency",
  currency: "COP",
  maximumFractionDigits: 0,
});

export function formatearPrecio(precio: number): string {
  return FORMATEADOR_PRECIO.format(precio);
}

const FORMATEADOR_FECHA = new Intl.DateTimeFormat("es-CO", {
  dateStyle: "medium",
  timeStyle: "short",
});

export function formatearFecha(fechaIso: string): string {
  return FORMATEADOR_FECHA.format(new Date(fechaIso));
}

export const ETIQUETA_TIPO_LICENCIA: Record<TipoLicenciaEnum, string> = {
  EXCLUSIVA: "Exclusiva",
  NO_EXCLUSIVA: "No exclusiva",
  COMERCIAL_LIMITADA: "Comercial limitada",
};

export const ETIQUETA_ESTADO_BEAT: Record<EstadoBeat, string> = {
  BORRADOR: "Borrador",
  PUBLICADO: "Publicado",
};

export const ETIQUETA_ESTADO_COMPRA: Record<EstadoCompra, string> = {
  PENDIENTE: "Pendiente",
  COMPLETADA: "Completada",
};

export const ETIQUETA_ESTADO_COLABORADOR: Record<EstadoColaborador, string> = {
  PENDIENTE: "Pendiente",
  ACEPTADO: "Aceptado",
  RECHAZADO: "Rechazado",
};

export const ETIQUETA_ROL_COLABORADOR: Record<RolColaborador, string> = {
  PRODUCTOR: "Productor",
  CO_PRODUCTOR: "Co-productor",
  VOCALISTA: "Vocalista",
  MEZCLA: "Mezcla",
  MASTERING: "Mastering",
};

export const ETIQUETA_ESTADO_ACUERDO: Record<EstadoAcuerdo, string> = {
  ABIERTO: "Abierto",
  CERRADO: "Cerrado",
};

export const ETIQUETA_TIPO_NOTIFICACION: Record<string, string> = {
  INVITACION_COLABORACION: "Invitación a colaborar",
  ACEPTACION_COLABORACION: "Aceptaron tu invitación",
  RECHAZO_COLABORACION: "Rechazaron tu invitación",
  COMPRA_COMPLETADA: "Compra completada",
};
