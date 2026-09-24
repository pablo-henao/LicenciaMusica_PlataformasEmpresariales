import { useCallback, useState } from "react";
import { descargarContrato, eliminarCompra, obtenerMisCompras } from "../../api/compras";
import type { CompraResponse, PaginaResponse } from "../../api/types";
import { ApiError } from "../../api/client";
import { FormAlert } from "../../components/FormAlert";
import { Pagination } from "../../components/Pagination";
import { useApiFetch } from "../../hooks/useApiFetch";
import {
  ETIQUETA_ESTADO_COMPRA,
  formatearFecha,
  formatearPrecio,
} from "../../utils/format";

const TAMANO = 12;
const VACIA: PaginaResponse<CompraResponse> = {
  contenido: [],
  pagina: 0,
  tamano: TAMANO,
  totalElementos: 0,
  totalPaginas: 0,
};

export function MisComprasPage() {
  const [pagina, setPagina] = useState(0);
  const [recargar, setRecargar] = useState(0);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);

  const fetcher = useCallback(() => obtenerMisCompras(pagina, TAMANO), [pagina]);
  const { datos, cargando, error } = useApiFetch(fetcher, [pagina, recargar], "No se pudo cargar tu historial.");
  const resultado = datos ?? VACIA;

  async function descargar(compra: CompraResponse) {
    setErrorAccion(null);
    try {
      await descargarContrato(compra);
    } catch (e) {
      setErrorAccion(e instanceof ApiError ? e.message : "No se pudo descargar el contrato.");
    }
  }

  async function cancelar(compra: CompraResponse) {
    if (!confirm(`¿Cancelar la compra #${compra.id} (${compra.beatTitulo})?`)) return;
    setErrorAccion(null);
    try {
      await eliminarCompra(compra.id);
      setRecargar((n) => n + 1);
    } catch (e) {
      setErrorAccion(e instanceof ApiError ? e.message : "No se pudo cancelar la compra.");
    }
  }

  return (
    <div>
      <div className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h1 className="text-2xl font-bold tracking-tight">Mis compras</h1>
        <p className="mt-1 text-sm text-neutral-600">
          Tu historial de licencias adquiridas. Cada compra completada trae su contrato en PDF.
        </p>
      </div>

      <div className="mt-5">
        {error && <FormAlert mensaje={error} />}
        {errorAccion && (
          <div className="mb-3">
            <FormAlert mensaje={errorAccion} />
          </div>
        )}
        {cargando && <p className="py-10 text-center text-neutral-500">Cargando...</p>}
        {!cargando && !error && resultado.contenido.length === 0 && (
          <div className="rounded-2xl border border-dashed border-neutral-300 bg-white py-12 text-center text-neutral-500">
            Aún no tienes compras. Explora la tienda y compra tu primera licencia.
          </div>
        )}
        <div className="space-y-3">
          {resultado.contenido.map((compra) => (
            <div
              key={compra.id}
              className="flex flex-col gap-3 rounded-2xl border border-neutral-200 bg-white p-4 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="font-bold">
                  #{compra.id} · {compra.beatTitulo}
                </p>
                <p className="mt-0.5 text-sm text-neutral-500">
                  {formatearPrecio(compra.precio)} · {formatearFecha(compra.fecha)} ·{" "}
                  <span
                    className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                      compra.estado === "COMPLETADA"
                        ? "bg-green-100 text-green-800"
                        : "bg-yellow-100 text-yellow-800"
                    }`}
                  >
                    {ETIQUETA_ESTADO_COMPRA[compra.estado]}
                  </span>
                </p>
              </div>
              <div className="flex gap-2">
                {compra.contratoDisponible && (
                  <button
                    type="button"
                    onClick={() => descargar(compra)}
                    className="rounded-full bg-neutral-900 px-4 py-1.5 text-sm font-medium text-white hover:bg-brand-700"
                  >
                    Contrato PDF
                  </button>
                )}
                {compra.estado === "PENDIENTE" && (
                  <button
                    type="button"
                    onClick={() => cancelar(compra)}
                    className="rounded-full border border-neutral-300 px-4 py-1.5 text-sm font-medium text-neutral-600 hover:bg-neutral-100"
                  >
                    Cancelar
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
        <Pagination pagina={pagina} totalPaginas={resultado.totalPaginas} onCambiar={setPagina} />
      </div>
    </div>
  );
}
