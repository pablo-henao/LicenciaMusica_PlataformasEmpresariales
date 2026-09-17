import type { TipoLicenciaEnum } from "../api/types";

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
