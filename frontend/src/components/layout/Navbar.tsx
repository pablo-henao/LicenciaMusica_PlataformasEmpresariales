import { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { contarNoLeidas } from "../../api/usuarios";
import { useAuth } from "../../context/useAuth";

const ETIQUETA_ROL: Record<string, string> = {
  PRODUCTOR: "Productor",
  COMPRADOR: "Comprador",
  ADMIN: "Admin",
};

function linkClase({ isActive }: { isActive: boolean }): string {
  return `rounded-full px-3 py-1.5 text-sm font-medium transition ${
    isActive ? "bg-neutral-900 text-white" : "text-neutral-600 hover:bg-neutral-100 hover:text-neutral-900"
  }`;
}

export function Navbar() {
  const { usuario, logout } = useAuth();
  const navegar = useNavigate();
  const [busqueda, setBusqueda] = useState("");
  const [noLeidas, setNoLeidas] = useState<number>(0);

  useEffect(() => {
    if (!usuario) {
      return;
    }
    let cancelado = false;
    contarNoLeidas()
      .then((n) => {
        if (!cancelado) setNoLeidas(n);
      })
      .catch(() => undefined);
    return () => {
      cancelado = true;
    };
  }, [usuario]);

  function cerrarSesion() {
    logout();
    navegar("/");
  }

  function buscar(evento: React.FormEvent) {
    evento.preventDefault();
    const q = busqueda.trim();
    navegar(q ? `/catalogo?titulo=${encodeURIComponent(q)}` : "/catalogo");
  }

  return (
    <header className="sticky top-3 z-40 mx-auto w-full max-w-6xl px-4 sm:px-6">
      <div className="flex items-center gap-3 rounded-2xl border border-neutral-200 bg-white/95 px-4 py-2.5 shadow-sm backdrop-blur">
        <Link to="/" className="flex items-center gap-2">
          <span className="flex h-7 w-7 items-center justify-center rounded-full bg-neutral-900 text-sm font-black text-white">
            ✺
          </span>
          <span className="text-lg font-bold tracking-tight">
            Licencia<span className="text-brand-600">+</span>
          </span>
        </Link>

        <nav className="ml-2 hidden items-center gap-1 lg:flex">
          <NavLink to="/" className={linkClase}>
            Inicio
          </NavLink>
          <NavLink to={usuario ? "/catalogo" : "/registro"} className={linkClase}>
            Beats
          </NavLink>
          <a
            href="/#licencias"
            className="rounded-full px-3 py-1.5 text-sm font-medium text-neutral-600 transition hover:bg-neutral-100 hover:text-neutral-900"
          >
            Licencias
          </a>
          <a
            href="/#splits"
            className="rounded-full px-3 py-1.5 text-sm font-medium text-neutral-600 transition hover:bg-neutral-100 hover:text-neutral-900"
          >
            Splits
          </a>
          <a
            href="/#nosotros"
            className="rounded-full px-3 py-1.5 text-sm font-medium text-neutral-600 transition hover:bg-neutral-100 hover:text-neutral-900"
          >
            Nosotros
          </a>
          <NavLink
            to={usuario?.rol === "PRODUCTOR" ? "/mis-beats" : "/registro"}
            className={linkClase}
          >
            Vende tu beat
          </NavLink>
        </nav>

        <div className="ml-auto flex items-center gap-2">
          {usuario && (
            <form onSubmit={buscar} className="hidden items-center gap-2 rounded-full bg-neutral-100 px-3 py-1.5 md:flex">
              <span className="text-neutral-400">⌕</span>
              <input
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                placeholder="Buscar"
                className="w-28 bg-transparent text-sm outline-none placeholder:text-neutral-400"
              />
            </form>
          )}

          {usuario ? (
            <>
              <Link
                to="/notificaciones"
                title="Notificaciones"
                className="relative flex h-9 w-9 items-center justify-center rounded-full border border-neutral-200 text-neutral-600 transition hover:bg-neutral-100"
              >
                ♡
                {noLeidas > 0 && (
                  <span className="absolute -top-1 -right-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-brand-600 px-1 text-[11px] font-bold text-white">
                    {noLeidas > 9 ? "9+" : noLeidas}
                  </span>
                )}
              </Link>
              <span className="hidden text-sm text-neutral-600 xl:inline">
                {usuario.nombre}{" "}
                <span className="rounded-full bg-brand-50 px-2 py-0.5 text-xs font-medium text-brand-700">
                  {ETIQUETA_ROL[usuario.rol] ?? usuario.rol}
                </span>
              </span>
              <button
                type="button"
                onClick={cerrarSesion}
                className="rounded-full border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
              >
                Salir
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="rounded-full px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
              >
                Iniciar sesión
              </Link>
              <Link
                to="/registro"
                className="rounded-full bg-neutral-900 px-4 py-1.5 text-sm font-medium text-white transition hover:bg-neutral-700"
              >
                Registrarse
              </Link>
            </>
          )}
        </div>
      </div>

      {usuario && (
        <nav className="mt-2 flex gap-1 overflow-x-auto rounded-2xl border border-neutral-200 bg-white/90 p-1.5">
          <NavLink to="/catalogo" className={linkClase}>
            Tienda
          </NavLink>
          {usuario.rol === "PRODUCTOR" && (
            <>
              <NavLink to="/mis-beats" className={linkClase}>
                Mis beats
              </NavLink>
              <NavLink to="/mis-ventas" className={linkClase}>
                Ventas
              </NavLink>
            </>
          )}
          <NavLink to="/mis-compras" className={linkClase}>
            Compras
          </NavLink>
          <NavLink to="/mis-invitaciones" className={linkClase}>
            Mis splits
          </NavLink>
          <NavLink to="/notificaciones" className={linkClase}>
            Avisos{noLeidas > 0 ? ` (${noLeidas})` : ""}
          </NavLink>
          {usuario.rol === "ADMIN" && (
            <NavLink to="/admin" className={linkClase}>
              Admin
            </NavLink>
          )}
        </nav>
      )}
    </header>
  );
}
