import { api, descargarArchivo } from "./client";
import type { CompraRequest, CompraResponse, PaginaResponse } from "./types";

export function crearCompra(datos: CompraRequest): Promise<CompraResponse> {
  return api.post<CompraResponse>("/api/compras", datos);
}

export function checkoutCompra(id: number): Promise<CompraResponse> {
  return api.patch<CompraResponse>(`/api/compras/${id}/checkout`);
}

export function obtenerMisCompras(page = 0, size = 20): Promise<PaginaResponse<CompraResponse>> {
  return api.get<PaginaResponse<CompraResponse>>("/api/compras", { page, size });
}

export function obtenerMisVentas(page = 0, size = 20): Promise<PaginaResponse<CompraResponse>> {
  return api.get<PaginaResponse<CompraResponse>>("/api/compras/mis-ventas", { page, size });
}

export function obtenerCompra(id: number): Promise<CompraResponse> {
  return api.get<CompraResponse>(`/api/compras/${id}`);
}

export function eliminarCompra(id: number): Promise<void> {
  return api.delete<void>(`/api/compras/${id}`);
}

export function descargarContrato(compra: CompraResponse): Promise<void> {
  return descargarArchivo(`/api/compras/${compra.id}/contrato`, `contrato-compra-${compra.id}.pdf`);
}
