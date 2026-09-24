import { api } from "./client";
import type {
  AcuerdoCreditosResponse,
  BeatResponse,
  CompraResponse,
  PaginaResponse,
} from "./types";

export function adminBeats(page = 0, size = 20): Promise<PaginaResponse<BeatResponse>> {
  return api.get<PaginaResponse<BeatResponse>>("/api/admin/beats", { page, size });
}

export function adminCompras(page = 0, size = 20): Promise<PaginaResponse<CompraResponse>> {
  return api.get<PaginaResponse<CompraResponse>>("/api/admin/compras", { page, size });
}

export function adminAcuerdos(page = 0, size = 20): Promise<PaginaResponse<AcuerdoCreditosResponse>> {
  return api.get<PaginaResponse<AcuerdoCreditosResponse>>("/api/admin/acuerdos-creditos", {
    page,
    size,
  });
}
