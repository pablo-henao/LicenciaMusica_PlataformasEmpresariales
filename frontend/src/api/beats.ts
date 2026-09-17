import { api } from "./client";
import type { BeatResponse, PaginaResponse } from "./types";

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
