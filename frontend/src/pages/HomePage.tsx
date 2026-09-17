import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

export function HomePage() {
  const { usuario } = useAuth();

  return (
    <div className="flex flex-col items-center gap-6 py-16 text-center">
      <span className="rounded-full bg-brand-50 px-3 py-1 text-xs font-medium tracking-wide text-brand-700 uppercase">
        Marketplace de licenciamiento musical
      </span>

      <h1 className="max-w-2xl text-4xl font-semibold text-neutral-900 sm:text-5xl">
        Licencia<span className="text-brand-600">+</span>
      </h1>

      <p className="max-w-lg text-neutral-600">
        Publica tus beats, define licencias claras y reparte los créditos con tus colaboradores —
        todo con respaldo de contrato.
      </p>

      {!usuario && (
        <div className="flex gap-3">
          <Link
            to="/registro"
            className="rounded-md bg-brand-600 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-brand-700"
          >
            Crear cuenta
          </Link>
          <Link
            to="/login"
            className="rounded-md border border-neutral-300 px-5 py-2.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
          >
            Iniciar sesión
          </Link>
        </div>
      )}

      {usuario && (
        <p className="text-sm text-neutral-500">
          El catálogo de beats está en camino — vuelve pronto para explorar y comprar licencias.
        </p>
      )}
    </div>
  );
}
