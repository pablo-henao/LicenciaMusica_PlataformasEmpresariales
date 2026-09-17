import { api } from "./client";
import type { TipoLicenciaResponse } from "./types";

/**
 * GET /api/licencias no tiene filtro por beat en el backend (devuelve todas las
 * licencias visibles para el usuario autenticado), asi que se filtra en el cliente.
 * Para el tamano de catalogo de este proyecto no hace falta paginar esta lista.
 */
export async function obtenerLicenciasDeBeat(beatId: number): Promise<TipoLicenciaResponse[]> {
  const todas = await api.get<TipoLicenciaResponse[]>("/api/licencias");
  return todas.filter((licencia) => licencia.beatId === beatId);
}
