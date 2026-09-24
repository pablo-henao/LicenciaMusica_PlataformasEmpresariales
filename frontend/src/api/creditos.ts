import { api } from "./client";
import type {
  AcuerdoCreditosEventoResponse,
  AcuerdoCreditosRequest,
  AcuerdoCreditosResponse,
  ColaboradorBeatRequest,
  ColaboradorBeatResponse,
  EstadoColaborador,
} from "./types";

export function obtenerColaboradores(): Promise<ColaboradorBeatResponse[]> {
  return api.get<ColaboradorBeatResponse[]>("/api/colaboradores");
}

export function obtenerMisInvitaciones(
  estado?: EstadoColaborador,
): Promise<ColaboradorBeatResponse[]> {
  return api.get<ColaboradorBeatResponse[]>("/api/colaboradores/mias", { estado });
}

export function invitarColaborador(datos: ColaboradorBeatRequest): Promise<ColaboradorBeatResponse> {
  return api.post<ColaboradorBeatResponse>("/api/colaboradores", datos);
}

export function actualizarColaborador(
  id: number,
  datos: ColaboradorBeatRequest,
): Promise<ColaboradorBeatResponse> {
  return api.put<ColaboradorBeatResponse>(`/api/colaboradores/${id}`, datos);
}

export function aceptarColaboracion(id: number): Promise<ColaboradorBeatResponse> {
  return api.patch<ColaboradorBeatResponse>(`/api/colaboradores/${id}/aceptar`);
}

export function rechazarColaboracion(id: number): Promise<ColaboradorBeatResponse> {
  return api.patch<ColaboradorBeatResponse>(`/api/colaboradores/${id}/rechazar`);
}

export function eliminarColaborador(id: number): Promise<void> {
  return api.delete<void>(`/api/colaboradores/${id}`);
}

export function abrirAcuerdo(datos: AcuerdoCreditosRequest): Promise<AcuerdoCreditosResponse> {
  return api.post<AcuerdoCreditosResponse>("/api/acuerdos-creditos", datos);
}

export function obtenerAcuerdos(): Promise<AcuerdoCreditosResponse[]> {
  return api.get<AcuerdoCreditosResponse[]>("/api/acuerdos-creditos");
}

export function eliminarAcuerdo(id: number): Promise<void> {
  return api.delete<void>(`/api/acuerdos-creditos/${id}`);
}

export function obtenerHistorialCreditos(beatId: number): Promise<AcuerdoCreditosEventoResponse[]> {
  return api.get<AcuerdoCreditosEventoResponse[]>(`/api/beats/${beatId}/historial-creditos`);
}
