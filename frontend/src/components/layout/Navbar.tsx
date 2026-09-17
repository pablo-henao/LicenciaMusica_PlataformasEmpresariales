import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";

const ETIQUETA_ROL: Record<string, string> = {
  PRODUCTOR: "Productor",
  COMPRADOR: "Comprador",
  ADMIN: "Admin",
};

export function Navbar() {
  const { usuario, logout } = useAuth();
  const navegar = useNavigate();

  function cerrarSesion() {
    logout();
    navegar("/");
  }

  return (
    <header className="border-b border-neutral-200 bg-white">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3 sm:px-6">
        <div className="flex items-center gap-6">
          <Link to="/" className="text-xl font-semibold text-brand-700">
            Licencia<span className="text-brand-500">+</span>
          </Link>

          {usuario && (
            <nav className="hidden items-center gap-4 text-sm font-medium text-neutral-600 sm:flex">
              <Link to="/catalogo" className="transition hover:text-brand-700">
                Catálogo
              </Link>
            </nav>
          )}
        </div>

        <nav className="flex items-center gap-3">
          {usuario ? (
            <>
              <span className="hidden text-sm text-neutral-600 sm:inline">
                {usuario.nombre}{" "}
                <span className="rounded-full bg-brand-50 px-2 py-0.5 text-xs font-medium text-brand-700">
                  {ETIQUETA_ROL[usuario.rol] ?? usuario.rol}
                </span>
              </span>
              <button
                type="button"
                onClick={cerrarSesion}
                className="rounded-md border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
              >
                Cerrar sesión
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="rounded-md px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
              >
                Iniciar sesión
              </Link>
              <Link
                to="/registro"
                className="rounded-md bg-brand-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700"
              >
                Registrarse
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
