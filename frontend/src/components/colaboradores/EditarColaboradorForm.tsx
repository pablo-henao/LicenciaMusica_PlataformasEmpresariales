import { useState, type FormEvent } from "react";
import { ApiError } from "../../api/client";
import { actualizarColaborador } from "../../api/colaboradores";
import type { ColaboradorBeatResponse, RolColaborador } from "../../api/types";
import { ETIQUETA_ROL_COLABORADOR, OPCIONES_ROL_COLABORADOR } from "../../utils/format";
import { FormAlert } from "../FormAlert";

interface EditarColaboradorFormProps {
  beatId: number;
  colaborador: ColaboradorBeatResponse;
  onGuardado: () => void;
  onCancelar: () => void;
}

export function EditarColaboradorForm({ beatId, colaborador, onGuardado, onCancelar }: EditarColaboradorFormProps) {
  const [rol, setRol] = useState<RolColaborador>(colaborador.rol);
  const [porcentaje, setPorcentaje] = useState(String(colaborador.porcentajePropuesto));
  const [error, setError] = useState<ApiError | null>(null);
  const [guardando, setGuardando] = useState(false);

  async function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    setError(null);
    setGuardando(true);

    try {
      await actualizarColaborador(colaborador.id, {
        beatId,
        usuarioId: colaborador.usuarioId,
        rol,
        porcentajePropuesto: Number(porcentaje),
      });
      onGuardado();
    } catch (err) {
      setError(err instanceof ApiError ? err : new ApiError(0, "No se pudo guardar el colaborador."));
      setGuardando(false);
    }
  }

  return (
    <form onSubmit={alEnviar} className="space-y-3 rounded-lg border border-brand-200 bg-brand-50/40 p-3">
      {error && <FormAlert mensaje={error.message} detalles={error.detalles} />}

      <p className="text-sm text-neutral-600">
        Editando a <span className="font-medium text-neutral-900">{colaborador.usuarioNombre}</span>. Cualquier
        cambio reabre el acuerdo y reinicia las aceptaciones de todos.
      </p>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label htmlFor="editarRolColaborador" className="block text-xs font-medium text-neutral-600">
            Rol
          </label>
          <select
            id="editarRolColaborador"
            value={rol}
            onChange={(e) => setRol(e.target.value as RolColaborador)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          >
            {OPCIONES_ROL_COLABORADOR.map((opcion) => (
              <option key={opcion} value={opcion}>
                {ETIQUETA_ROL_COLABORADOR[opcion]}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="editarPorcentajeColaborador" className="block text-xs font-medium text-neutral-600">
            Porcentaje
          </label>
          <input
            id="editarPorcentajeColaborador"
            type="number"
            required
            min={0.01}
            max={100}
            step="0.01"
            value={porcentaje}
            onChange={(e) => setPorcentaje(e.target.value)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>
      </div>

      <div className="flex gap-2">
        <button
          type="submit"
          disabled={guardando}
          className="rounded-md bg-brand-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
        >
          {guardando ? "Guardando..." : "Guardar cambios"}
        </button>
        <button
          type="button"
          onClick={onCancelar}
          className="rounded-md border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
        >
          Cancelar
        </button>
      </div>
    </form>
  );
}
