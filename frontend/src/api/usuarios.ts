import { api } from "./client";
import type { Usuario } from "./types";

/**
 * Busqueda puntual por email exacto (para invitar colaboradores). El backend
 * a proposito no tiene un GET que liste todos los usuarios — ver docs/bloque-06.
 */
export function buscarUsuarioPorEmail(email: string): Promise<Usuario> {
  return api.get<Usuario>("/api/usuarios/buscar", { email });
}

export function obtenerUsuarioPorId(id: number): Promise<Usuario> {
  return api.get<Usuario>(`/api/usuarios/${id}`);
}
