import { useCallback, useState } from "react";
import { ApiError } from "../../api/client";
import { eliminarLicencia, obtenerLicenciasDeBeat } from "../../api/licencias";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_TIPO_LICENCIA, formatearPrecio } from "../../utils/format";
import { FormAlert } from "../FormAlert";
import { LicenciaForm } from "./LicenciaForm";

interface LicenciasManagerProps {
  beatId: number;
}

export function LicenciasManager({ beatId }: LicenciasManagerProps) {
  const [refrescar, setRefrescar] = useState(0);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [editando, setEditando] = useState<number | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);

  const fetcher = useCallback(() => obtenerLicenciasDeBeat(beatId), [beatId]);
  const { datos, cargando, error } = useApiFetch(
    fetcher,
    [beatId, refrescar],
    "No se pudieron cargar las licencias.",
  );
  const licencias = datos ?? [];

  function alGuardar() {
    setMostrarForm(false);
    setEditando(null);
    setRefrescar((valor) => valor + 1);
  }

  async function eliminar(id: number) {
    if (!window.confirm("¿Eliminar esta licencia?")) {
      return;
    }

    setErrorAccion(null);
    try {
      await eliminarLicencia(id);
      setRefrescar((valor) => valor + 1);
    } catch (err) {
      setErrorAccion(err instanceof ApiError ? err.message : "No se pudo eliminar la licencia.");
    }
  }

  return (
    <section>
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-neutral-900">Licencias</h2>
        <button
          type="button"
          onClick={() => {
            setMostrarForm((valor) => !valor);
            setEditando(null);
          }}
          className="rounded-md bg-brand-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700"
        >
          {mostrarForm ? "Cancelar" : "+ Nueva licencia"}
        </button>
      </div>

      {error && <div className="mt-3"><FormAlert mensaje={error} /></div>}
      {errorAccion && <div className="mt-3"><FormAlert mensaje={errorAccion} /></div>}

      {mostrarForm && (
        <div className="mt-3">
          <LicenciaForm beatId={beatId} onGuardado={alGuardar} onCancelar={() => setMostrarForm(false)} />
        </div>
      )}

      {cargando && <p className="mt-3 text-center text-neutral-500">Cargando...</p>}

      {!cargando && licencias.length === 0 && !mostrarForm && (
        <p className="mt-3 text-sm text-neutral-500">Todavía no definiste licencias para este beat.</p>
      )}

      <div className="mt-3 space-y-2">
        {licencias.map((licencia) =>
          editando === licencia.id ? (
            <LicenciaForm
              key={licencia.id}
              beatId={beatId}
              licencia={licencia}
              onGuardado={alGuardar}
              onCancelar={() => setEditando(null)}
            />
          ) : (
            <div
              key={licencia.id}
              className="flex flex-col gap-2 rounded-lg border border-neutral-200 bg-white p-3 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="font-medium text-neutral-900">
                  {ETIQUETA_TIPO_LICENCIA[licencia.tipo]} — {formatearPrecio(licencia.precio)}
                </p>
                {licencia.condiciones && <p className="mt-1 text-sm text-neutral-600">{licencia.condiciones}</p>}
              </div>
              <div className="flex shrink-0 gap-2">
                <button
                  type="button"
                  onClick={() => {
                    setEditando(licencia.id);
                    setMostrarForm(false);
                  }}
                  className="rounded-md border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
                >
                  Editar
                </button>
                <button
                  type="button"
                  onClick={() => eliminar(licencia.id)}
                  className="rounded-md border border-red-200 px-3 py-1.5 text-sm font-medium text-red-700 transition hover:bg-red-50"
                >
                  Eliminar
                </button>
              </div>
            </div>
          ),
        )}
      </div>
    </section>
  );
}
