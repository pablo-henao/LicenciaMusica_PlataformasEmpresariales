import { useEffect, useState, type ReactNode } from "react";
import { api, getToken, limpiarToken, setToken } from "../api/client";
import type { AuthResponse, Rol, Usuario } from "../api/types";
import { AuthContext } from "./authContextDefinition";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [cargando, setCargando] = useState(() => Boolean(getToken()));

  useEffect(() => {
    if (!getToken()) {
      return;
    }

    api
      .get<Usuario>("/api/auth/me")
      .then(setUsuario)
      .catch(() => limpiarToken())
      .finally(() => setCargando(false));
  }, []);

  async function login(email: string, password: string) {
    const respuesta = await api.post<AuthResponse>("/api/auth/login", { email, password });
    setToken(respuesta.token);
    setUsuario(await api.get<Usuario>("/api/auth/me"));
  }

  async function registrar(nombre: string, email: string, password: string, rol: Rol) {
    const respuesta = await api.post<AuthResponse>("/api/auth/register", { nombre, email, password, rol });
    setToken(respuesta.token);
    setUsuario(await api.get<Usuario>("/api/auth/me"));
  }

  function logout() {
    limpiarToken();
    setUsuario(null);
  }

  return (
    <AuthContext.Provider value={{ usuario, cargando, login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
