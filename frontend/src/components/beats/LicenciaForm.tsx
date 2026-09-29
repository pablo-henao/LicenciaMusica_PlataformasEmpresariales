import { useState, type FormEvent } from "react";
import { ApiError } from "../../api/client";
import { actualizarLicencia, crearLicencia } from "../../api/licencias";
import type { TipoLicenciaEnum, TipoLicenciaResponse } from "../../api/types";
import { ETIQUETA_TIPO_LICENCIA, OPCIONES_TIPO_LICENCIA } from "../../utils/format";
import { FormAlert } from "../FormAlert";

interface LicenciaFormProps {
  beatId: number;
  licencia?: TipoLicenciaResponse;
  onGuardado: () => void;
  onCancelar?: () => void;
}

export function LicenciaForm({ beatId, licencia, onGuardado, onCancelar }: LicenciaFormProps) {
  const [tipo, setTipo] = useState<TipoLicenciaEnum>(licencia?.tipo ?? "NO_EXCLUSIVA");
  const [precio, setPrecio] = useState(licencia ? String(licencia.precio) : "");
  const [condiciones, setCondiciones] = useState(licencia?.condiciones ?? "");
  const [error, setError] = useState<ApiError | null>(null);
  const [guardando, setGuardando] = useState(false);

  async function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    setError(null);
    setGuardando(true);

    try {
      const datos = { beatId, tipo, precio: Number(precio), condiciones: condiciones.trim() || null };
      if (licencia) {
        await actualizarLicencia(licencia.id, datos);
      } else {
        await crearLicencia(datos);
      }
      onGuardado();
    } catch (err) {
      setError(err instanceof ApiError ? err : new ApiError(0, "No se pudo guardar la licencia."));
      setGuardando(false);
    }
  }

  return (
    <form onSubmit={alEnviar} className="space-y-3 rounded-lg border border-brand-200 bg-brand-50/40 p-3">
      {error && <FormAlert mensaje={error.message} detalles={error.detalles} />}

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label htmlFor="tipoLicencia" className="block text-xs font-medium text-neutral-600">
            Tipo
          </label>
          <select
            id="tipoLicencia"
            value={tipo}
            onChange={(e) => setTipo(e.target.value as TipoLicenciaEnum)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          >
            {OPCIONES_TIPO_LICENCIA.map((opcion) => (
              <option key={opcion} value={opcion}>
                {ETIQUETA_TIPO_LICENCIA[opcion]}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="precioLicencia" className="block text-xs font-medium text-neutral-600">
            Precio (COP)
          </label>
          <input
            id="precioLicencia"
            type="number"
            required
            min={1}
            value={precio}
            onChange={(e) => setPrecio(e.target.value)}
            className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
          />
        </div>
      </div>

      <div>
        <label htmlFor="condicionesLicencia" className="block text-xs font-medium text-neutral-600">
          Condiciones <span className="font-normal text-neutral-400">(opcional)</span>
        </label>
        <textarea
          id="condicionesLicencia"
          rows={2}
          value={condiciones}
          onChange={(e) => setCondiciones(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div className="flex gap-2">
        <button
          type="submit"
          disabled={guardando}
          className="rounded-md bg-brand-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
        >
          {guardando ? "Guardando..." : licencia ? "Guardar cambios" : "Crear licencia"}
        </button>
        {onCancelar && (
          <button
            type="button"
            onClick={onCancelar}
            className="rounded-md border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
          >
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}
