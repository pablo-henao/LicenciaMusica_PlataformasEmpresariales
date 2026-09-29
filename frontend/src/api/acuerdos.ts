import { api } from "./client";
import type { AcuerdoCreditosEventoResponse, AcuerdoCreditosResponse } from "./types";

/**
 * GET /api/acuerdos-creditos tampoco filtra por beat — se filtra en el cliente.
 * AcuerdoCreditos es 1:1 con Beat, asi que a lo sumo hay una coincidencia.
 */
export async function obtenerAcuerdoDeBeat(beatId: number): Promise<AcuerdoCreditosResponse | null> {
  const todos = await api.get<AcuerdoCreditosResponse[]>("/api/acuerdos-creditos");
  return todos.find((acuerdo) => acuerdo.beatId === beatId) ?? null;
}

export function abrirAcuerdo(beatId: number): Promise<AcuerdoCreditosResponse> {
  return api.post<AcuerdoCreditosResponse>("/api/acuerdos-creditos", { beatId });
}

export function obtenerHistorialCreditos(beatId: number): Promise<AcuerdoCreditosEventoResponse[]> {
  return api.get<AcuerdoCreditosEventoResponse[]>(`/api/beats/${beatId}/historial-creditos`);
}
