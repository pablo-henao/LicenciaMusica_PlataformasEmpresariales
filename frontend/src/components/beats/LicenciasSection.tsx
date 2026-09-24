import { useState } from "react";
import {
  actualizarLicencia,
  crearLicencia,
  eliminarLicencia,
} from "../../api/licencias";
import type { TipoLicenciaEnum, TipoLicenciaRequest, TipoLicenciaResponse } from "../../api/types";
import { ApiError } from "../../api/client";
import { FormAlert } from "../../components/FormAlert";
import { ETIQUETA_TIPO_LICENCIA, formatearPrecio } from "../../utils/format";

const TIPOS: TipoLicenciaEnum[] = ["NO_EXCLUSIVA", "COMERCIAL_LIMITADA", "EXCLUSIVA"];

export function LicenciasSection({
  beatId,
  licencias,
  onCambio,
}: {
  beatId: number;
  licencias: TipoLicenciaResponse[];
  onCambio: () => void;
}) {
  const [mostrarForm, setMostrarForm] = useState(false);
  const [editando, setEditando] = useState<TipoLicenciaResponse | null>(null);
  const [tipo, setTipo] = useState<TipoLicenciaEnum>("NO_EXCLUSIVA");
  const [precio, setPrecio] = useState("49900");
  const [condiciones, setCondiciones] = useState("");
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function abrirCrear() {
    setEditando(null);
    setTipo("NO_EXCLUSIVA");
    setPrecio("49900");
    setCondiciones("");
    setError(null);
    setMostrarForm(true);
  }

  function abrirEditar(lic: TipoLicenciaResponse) {
    setEditando(lic);
    setTipo(lic.tipo);
    setPrecio(String(lic.precio));
    setCondiciones(lic.condiciones ?? "");
    setError(null);
    setMostrarForm(true);
  }

  async function guardar(evento: React.FormEvent) {
    evento.preventDefault();
    setGuardando(true);
    setError(null);
    const datos: TipoLicenciaRequest = {
      beatId,
      tipo,
      precio: Number(precio),
      condiciones: condiciones.trim() || undefined,
    };
    try {
      if (editando) {
        await actualizarLicencia(editando.id, datos);
      } else {
        await crearLicencia(datos);
      }
      setMostrarForm(false);
      onCambio();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "No se pudo guardar la licencia.");
    } finally {
      setGuardando(false);
    }
  }

  async function eliminar(id: number) {
    if (!confirm("¿Eliminar esta licencia?")) return;
    setError(null);
    try {
      await eliminarLicencia(id);
      onCambio();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "No se pudo eliminar la licencia.");
    }
  }

  return (
    <section className="rounded-2xl border border-neutral-200 bg-white p-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h2 className="font-bold">Licencias y precios</h2>
          <p className="text-sm text-neutral-500">Define qué puede comprar cada cliente.</p>
        </div>
        <button
          type="button"
          onClick={abrirCrear}
          className="rounded-full bg-neutral-900 px-4 py-1.5 text-sm font-medium text-white hover:bg-brand-700"
        >
          + Licencia
        </button>
      </div>

      {error && (
        <div className="mt-3">
          <FormAlert mensaje={error} />
        </div>
      )}

      {mostrarForm && (
        <form onSubmit={guardar} className="mt-4 grid gap-3 rounded-xl bg-neutral-50 p-4 sm:grid-cols-3">
          <div>
            <label className="block text-xs font-medium text-neutral-600">Tipo *</label>
            <select
              value={tipo}
              onChange={(e) => setTipo(e.target.value as TipoLicenciaEnum)}
              className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm"
            >
              {TIPOS.map((t) => (
                <option key={t} value={t}>
                  {ETIQUETA_TIPO_LICENCIA[t]}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-neutral-600">Precio (COP) *</label>
            <input
              type="number"
              min={1}
              value={precio}
              onChange={(e) => setPrecio(e.target.value)}
              required
              className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm"
            />
          </div>
          <div className="sm:col-span-3">
            <label className="block text-xs font-medium text-neutral-600">Condiciones</label>
            <textarea
              value={condiciones}
              onChange={(e) => setCondiciones(e.target.value)}
              rows={2}
              placeholder="Ej: 50.000 streams, 1 videoclip, crédito obligatorio al productor."
              className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm"
            />
          </div>
          <div className="flex gap-2 sm:col-span-3">
            <button
              type="submit"
              disabled={guardando}
              className="rounded-full bg-brand-600 px-5 py-1.5 text-sm font-medium text-white hover:bg-brand-700 disabled:opacity-50"
            >
              {guardando ? "Guardando..." : editando ? "Guardar" : "Crear"}
            </button>
            <button
              type="button"
              onClick={() => setMostrarForm(false)}
              className="rounded-full border border-neutral-300 px-5 py-1.5 text-sm text-neutral-600 hover:bg-neutral-100"
            >
              Cancelar
            </button>
          </div>
        </form>
      )}

      <div className="mt-4 space-y-2">
        {licencias.length === 0 && (
          <p className="rounded-xl bg-neutral-100 p-3 text-sm text-neutral-500">
            Sin licencias todavía. Crea al menos una para poder vender.
          </p>
        )}
        {licencias.map((lic) => (
          <div
            key={lic.id}
            className="flex flex-col gap-2 rounded-xl border border-neutral-200 p-3 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <p className="text-sm font-bold">{ETIQUETA_TIPO_LICENCIA[lic.tipo]}</p>
              {lic.condiciones && <p className="text-xs text-neutral-500">{lic.condiciones}</p>}
              <p className="text-sm font-bold text-brand-700">{formatearPrecio(lic.precio)}</p>
            </div>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => abrirEditar(lic)}
                className="rounded-full border border-neutral-300 px-3 py-1 text-xs font-medium hover:bg-neutral-100"
              >
                Editar
              </button>
              <button
                type="button"
                onClick={() => eliminar(lic.id)}
                className="rounded-full border border-red-200 px-3 py-1 text-xs font-medium text-red-600 hover:bg-red-50"
              >
                Eliminar
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
