import { useState, type FormEvent } from "react";
import { ApiError } from "../../api/client";
import { invitarColaborador } from "../../api/colaboradores";
import { buscarUsuarioPorEmail } from "../../api/usuarios";
import type { RolColaborador } from "../../api/types";
import { ETIQUETA_ROL_COLABORADOR, OPCIONES_ROL_COLABORADOR } from "../../utils/format";
import { FormAlert } from "../FormAlert";

interface InvitarColaboradorFormProps {
  beatId: number;
  onInvitado: () => void;
}

export function InvitarColaboradorForm({ beatId, onInvitado }: InvitarColaboradorFormProps) {
  const [email, setEmail] = useState("");
  const [rol, setRol] = useState<RolColaborador>("VOCALISTA");
  const [porcentaje, setPorcentaje] = useState("");
  const [error, setError] = useState<ApiError | null>(null);
  const [enviando, setEnviando] = useState(false);

  async function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    setError(null);
    setEnviando(true);

    try {
      const usuario = await buscarUsuarioPorEmail(email.trim());
      await invitarColaborador({
        beatId,
        usuarioId: usuario.id,
        rol,
        porcentajePropuesto: Number(porcentaje),
      });
      onInvitado();
    } catch (err) {
      setError(err instanceof ApiError ? err : new ApiError(0, "No se pudo invitar al colaborador."));
      setEnviando(false);
    }
  }

  return (
    <form onSubmit={alEnviar} className="space-y-3 rounded-2xl border border-brand-200 bg-brand-50/40 p-4">
      {error && <FormAlert mensaje={error.message} detalles={error.detalles} />}

      <div>
        <label htmlFor="emailColaborador" className="block text-xs font-medium text-neutral-600">
          Email del colaborador
        </label>
        <input
          id="emailColaborador"
          type="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="colaborador@ejemplo.com"
          className="mt-1 w-full rounded-xl border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label htmlFor="rolColaborador" className="block text-xs font-medium text-neutral-600">
            Rol
          </label>
          <select
            id="rolColaborador"
            value={rol}
            onChange={(e) => setRol(e.target.value as RolColaborador)}
            className="mt-1 w-full rounded-xl border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          >
            {OPCIONES_ROL_COLABORADOR.map((opcion) => (
              <option key={opcion} value={opcion}>
                {ETIQUETA_ROL_COLABORADOR[opcion]}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="porcentajeColaborador" className="block text-xs font-medium text-neutral-600">
            Porcentaje
          </label>
          <input
            id="porcentajeColaborador"
            type="number"
            required
            min={0.01}
            max={100}
            step="0.01"
            value={porcentaje}
            onChange={(e) => setPorcentaje(e.target.value)}
            className="mt-1 w-full rounded-xl border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>
      </div>

      <button
        type="submit"
        disabled={enviando}
        className="rounded-full bg-brand-600 px-4 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
      >
        {enviando ? "Invitando..." : "Invitar"}
      </button>
    </form>
  );
}
