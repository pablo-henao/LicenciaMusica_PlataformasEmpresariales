import { api } from "./client";
import type { NotificacionResponse, PaginaResponse } from "./types";

export function obtenerMisNotificaciones(
  page = 0,
  size = 20,
): Promise<PaginaResponse<NotificacionResponse>> {
  return api.get<PaginaResponse<NotificacionResponse>>("/api/notificaciones", { page, size });
}

export function contarNoLeidas(): Promise<number> {
  return api.get<number>("/api/notificaciones/no-leidas/contador");
}

export function marcarNotificacionLeida(id: number): Promise<NotificacionResponse> {
  return api.patch<NotificacionResponse>(`/api/notificaciones/${id}/leer`);
}
