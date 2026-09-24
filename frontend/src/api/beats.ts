import { api } from "./client";
import type { BeatRequest, BeatResponse, PaginaResponse } from "./types";

export interface FiltrosCatalogo {
  genero?: string;
  titulo?: string;
  bpmMin?: number;
  bpmMax?: number;
  page?: number;
  size?: number;
}

export function obtenerCatalogo(filtros: FiltrosCatalogo): Promise<PaginaResponse<BeatResponse>> {
  return api.get<PaginaResponse<BeatResponse>>("/api/beats", {
    genero: filtros.genero,
    titulo: filtros.titulo,
    bpmMin: filtros.bpmMin,
    bpmMax: filtros.bpmMax,
    page: filtros.page,
    size: filtros.size,
  });
}

export function obtenerBeatPorId(id: number): Promise<BeatResponse> {
  return api.get<BeatResponse>(`/api/beats/${id}`);
}

export function obtenerMisBeats(page = 0, size = 20): Promise<PaginaResponse<BeatResponse>> {
  return api.get<PaginaResponse<BeatResponse>>("/api/beats/mios", { page, size });
}

export function crearBeat(datos: BeatRequest): Promise<BeatResponse> {
  return api.post<BeatResponse>("/api/beats", datos);
}

export function actualizarBeat(id: number, datos: BeatRequest): Promise<BeatResponse> {
  return api.put<BeatResponse>(`/api/beats/${id}`, datos);
}

export function publicarBeat(id: number): Promise<BeatResponse> {
  return api.patch<BeatResponse>(`/api/beats/${id}/publicar`);
}

export function eliminarBeat(id: number): Promise<void> {
  return api.delete<void>(`/api/beats/${id}`);
}
