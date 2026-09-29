import { api } from "./client";
import type { ColaboradorBeatResponse, EstadoColaborador, RolColaborador } from "./types";

export interface ColaboradorBeatRequestBody {
  beatId: number;
  usuarioId: number;
  rol: RolColaborador;
  porcentajePropuesto: number;
}

/**
 * GET /api/colaboradores tampoco filtra por beat en el backend (mismo caso que
 * las licencias) — se filtra en el cliente.
 */
export async function obtenerColaboradoresDeBeat(beatId: number): Promise<ColaboradorBeatResponse[]> {
  const todos = await api.get<ColaboradorBeatResponse[]>("/api/colaboradores");
  return todos.filter((colaborador) => colaborador.beatId === beatId);
}

export function invitarColaborador(datos: ColaboradorBeatRequestBody): Promise<ColaboradorBeatResponse> {
  return api.post<ColaboradorBeatResponse>("/api/colaboradores", datos);
}

// el backend ignora beatId/usuarioId en la actualizacion (no se reasignan via PUT).
export function actualizarColaborador(
  id: number,
  datos: ColaboradorBeatRequestBody,
): Promise<ColaboradorBeatResponse> {
  return api.put<ColaboradorBeatResponse>(`/api/colaboradores/${id}`, datos);
}

export function eliminarColaborador(id: number): Promise<void> {
  return api.delete<void>(`/api/colaboradores/${id}`);
}

export function aceptarColaboracion(id: number): Promise<ColaboradorBeatResponse> {
  return api.patch<ColaboradorBeatResponse>(`/api/colaboradores/${id}/aceptar`);
}

export function rechazarColaboracion(id: number): Promise<ColaboradorBeatResponse> {
  return api.patch<ColaboradorBeatResponse>(`/api/colaboradores/${id}/rechazar`);
}

export function obtenerMisInvitaciones(estado?: EstadoColaborador): Promise<ColaboradorBeatResponse[]> {
  return api.get<ColaboradorBeatResponse[]>("/api/colaboradores/mias", { estado });
}
