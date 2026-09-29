import { api } from "./client";
import type { TipoLicenciaEnum, TipoLicenciaResponse } from "./types";

/**
 * GET /api/licencias no tiene filtro por beat en el backend (devuelve todas las
 * licencias visibles para el usuario autenticado), asi que se filtra en el cliente.
 * Para el tamano de catalogo de este proyecto no hace falta paginar esta lista.
 */
export async function obtenerLicenciasDeBeat(beatId: number): Promise<TipoLicenciaResponse[]> {
  const todas = await api.get<TipoLicenciaResponse[]>("/api/licencias");
  return todas.filter((licencia) => licencia.beatId === beatId);
}

export interface TipoLicenciaRequestBody {
  beatId: number;
  tipo: TipoLicenciaEnum;
  precio: number;
  condiciones: string | null;
}

export function crearLicencia(datos: TipoLicenciaRequestBody): Promise<TipoLicenciaResponse> {
  return api.post<TipoLicenciaResponse>("/api/licencias", datos);
}

// el backend ignora beatId en la actualizacion (el beat asociado no se reasigna via PUT),
// pero el DTO lo exige igual — se manda el mismo valor que ya tenia la licencia.
export function actualizarLicencia(id: number, datos: TipoLicenciaRequestBody): Promise<TipoLicenciaResponse> {
  return api.put<TipoLicenciaResponse>(`/api/licencias/${id}`, datos);
}

export function eliminarLicencia(id: number): Promise<void> {
  return api.delete<void>(`/api/licencias/${id}`);
}
