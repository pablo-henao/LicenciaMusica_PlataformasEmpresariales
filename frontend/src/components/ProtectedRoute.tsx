import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import type { Rol } from "../api/types";

interface ProtectedRouteProps {
  children: ReactNode;
  rolesPermitidos?: Rol[];
}

export function ProtectedRoute({ children, rolesPermitidos }: ProtectedRouteProps) {
  const { usuario, cargando } = useAuth();

  if (cargando) {
    return (
      <div className="flex justify-center py-16 text-neutral-500">Cargando...</div>
    );
  }

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  if (rolesPermitidos && !rolesPermitidos.includes(usuario.rol)) {
    return (
      <div className="mx-auto max-w-md py-16 text-center">
        <h1 className="text-xl font-semibold text-neutral-900">Acceso no disponible</h1>
        <p className="mt-2 text-neutral-600">Tu cuenta no tiene permiso para ver esta sección.</p>
      </div>
    );
  }

  return <>{children}</>;
}
