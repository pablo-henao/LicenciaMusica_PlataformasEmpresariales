import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import { ApiError } from "../../api/client";
import { FormAlert } from "../../components/FormAlert";
import type { Rol } from "../../api/types";

export function RegisterPage() {
  const { registrar } = useAuth();
  const navegar = useNavigate();

  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rol, setRol] = useState<Rol>("PRODUCTOR");
  const [error, setError] = useState<ApiError | null>(null);
  const [enviando, setEnviando] = useState(false);

  async function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    setError(null);
    setEnviando(true);

    try {
      await registrar(nombre, email, password, rol);
      navegar("/");
    } catch (err) {
      setError(err instanceof ApiError ? err : new ApiError(0, "No se pudo completar el registro"));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="mx-auto max-w-sm">
      <h1 className="text-2xl font-semibold text-neutral-900">Crear cuenta</h1>
      <p className="mt-1 text-sm text-neutral-600">Únete a Licencia+ como productor o comprador.</p>

      <form onSubmit={alEnviar} className="mt-6 space-y-4">
        {error && <FormAlert mensaje={error.message} detalles={error.detalles} />}

        <div>
          <label htmlFor="nombre" className="block text-sm font-medium text-neutral-700">
            Nombre
          </label>
          <input
            id="nombre"
            type="text"
            required
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>

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
            minLength={6}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>

        <fieldset>
          <legend className="block text-sm font-medium text-neutral-700">Quiero registrarme como</legend>
          <div className="mt-2 grid grid-cols-2 gap-3">
            <label
              className={`cursor-pointer rounded-md border px-3 py-2 text-center text-sm font-medium transition ${
                rol === "PRODUCTOR"
                  ? "border-brand-500 bg-brand-50 text-brand-700"
                  : "border-neutral-300 text-neutral-600 hover:bg-neutral-50"
              }`}
            >
              <input
                type="radio"
                name="rol"
                value="PRODUCTOR"
                checked={rol === "PRODUCTOR"}
                onChange={() => setRol("PRODUCTOR")}
                className="sr-only"
              />
              Productor
            </label>
            <label
              className={`cursor-pointer rounded-md border px-3 py-2 text-center text-sm font-medium transition ${
                rol === "COMPRADOR"
                  ? "border-brand-500 bg-brand-50 text-brand-700"
                  : "border-neutral-300 text-neutral-600 hover:bg-neutral-50"
              }`}
            >
              <input
                type="radio"
                name="rol"
                value="COMPRADOR"
                checked={rol === "COMPRADOR"}
                onChange={() => setRol("COMPRADOR")}
                className="sr-only"
              />
              Comprador
            </label>
          </div>
        </fieldset>

        <button
          type="submit"
          disabled={enviando}
          className="w-full rounded-md bg-brand-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
        >
          {enviando ? "Creando cuenta..." : "Crear cuenta"}
        </button>
      </form>

      <p className="mt-4 text-center text-sm text-neutral-600">
        ¿Ya tienes cuenta?{" "}
        <Link to="/login" className="font-medium text-brand-700 hover:underline">
          Inicia sesión
        </Link>
      </p>
    </div>
  );
}
