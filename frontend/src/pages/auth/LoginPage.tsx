import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import { ApiError } from "../../api/client";
import { FormAlert } from "../../components/FormAlert";

export function LoginPage() {
  const { login } = useAuth();
  const navegar = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<ApiError | null>(null);
  const [enviando, setEnviando] = useState(false);

  async function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    setError(null);
    setEnviando(true);

    try {
      await login(email, password);
      navegar("/");
    } catch (err) {
      setError(err instanceof ApiError ? err : new ApiError(0, "No se pudo iniciar sesión"));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="mx-auto max-w-sm">
      <h1 className="text-2xl font-semibold text-neutral-900">Iniciar sesión</h1>
      <p className="mt-1 text-sm text-neutral-600">Entra a tu cuenta de Licencia+.</p>

      <form onSubmit={alEnviar} className="mt-6 space-y-4">
        {error && <FormAlert mensaje={error.message} detalles={error.detalles} />}

        <div>
          <label htmlFor="email" className="block text-sm font-medium text-neutral-700">
            Email
          </label>
          <input
            id="email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-neutral-700">
            Contraseña
          </label>
          <input
            id="password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>

        <button
          type="submit"
          disabled={enviando}
          className="w-full rounded-md bg-brand-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
        >
          {enviando ? "Entrando..." : "Iniciar sesión"}
        </button>
      </form>

      <p className="mt-4 text-center text-sm text-neutral-600">
        ¿No tienes cuenta?{" "}
        <Link to="/registro" className="font-medium text-brand-700 hover:underline">
          Regístrate
        </Link>
      </p>
    </div>
  );
}
