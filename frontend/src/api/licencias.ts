import { api } from "./client";
import type { TipoLicenciaRequest, TipoLicenciaResponse } from "./types";

/**
 * Usa el filtro ?beatId del backend (si el backend es anterior y lo ignora,
 * igual funciona porque se filtra en el cliente como respaldo).
 */
export async function obtenerLicenciasDeBeat(beatId: number): Promise<TipoLicenciaResponse[]> {
  const lista = await api.get<TipoLicenciaResponse[]>("/api/licencias", { beatId });
  const filtradas = lista.filter((licencia) => licencia.beatId === beatId);
  // Si el backend ya filtró, la lista viene exacta; si no, el filtro cliente la deja exacta.
  return lista.length > 0 && filtradas.length === 0 ? lista : filtradas;
}

export function crearLicencia(datos: TipoLicenciaRequest): Promise<TipoLicenciaResponse> {
  return api.post<TipoLicenciaResponse>("/api/licencias", datos);
}

export function actualizarLicencia(
  id: number,
  datos: TipoLicenciaRequest,
): Promise<TipoLicenciaResponse> {
  return api.put<TipoLicenciaResponse>(`/api/licencias/${id}`, datos);
}

export function eliminarLicencia(id: number): Promise<void> {
  return api.delete<void>(`/api/licencias/${id}`);
}
