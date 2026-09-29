import { useCallback, useState } from "react";
import { obtenerMisVentas } from "../../api/compras";
import type { CompraResponse, PaginaResponse } from "../../api/types";
import { FormAlert } from "../../components/FormAlert";
import { Pagination } from "../../components/Pagination";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_ESTADO_COMPRA, formatearFecha, formatearPrecio } from "../../utils/format";

const TAMANO = 12;
const VACIA: PaginaResponse<CompraResponse> = {
  contenido: [],
  pagina: 0,
  tamano: TAMANO,
  totalElementos: 0,
  totalPaginas: 0,
};

export function MisVentasPage() {
  const [pagina, setPagina] = useState(0);

  const fetcher = useCallback(() => obtenerMisVentas(pagina, TAMANO), [pagina]);
  const { datos, cargando, error } = useApiFetch(fetcher, [pagina], "No se pudieron cargar tus ventas.");
  const resultado = datos ?? VACIA;

  const total = resultado.contenido
    .filter((c) => c.estado === "COMPLETADA")
    .reduce((acc, c) => acc + c.precio, 0);

  return (
    <div>
      <div className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h1 className="text-2xl font-bold tracking-tight">Mis ventas</h1>
        <p className="mt-1 text-sm text-neutral-600">
          Compras de licencias de tus beats. {resultado.totalElementos} venta(s) en total
          {total > 0 && (
            <>
              {" "}· <span className="font-bold text-green-700">{formatearPrecio(total)}</span> en esta página
            </>
          )}
          .
        </p>
      </div>

      <div className="mt-5">
        {error && <FormAlert mensaje={error} />}
        {cargando && <p className="py-10 text-center text-neutral-500">Cargando...</p>}
        {!cargando && !error && resultado.contenido.length === 0 && (
          <div className="rounded-2xl border border-dashed border-neutral-300 bg-white py-12 text-center text-neutral-500">
            Aún no vendes licencias. Publica un beat para empezar.
          </div>
        )}
        <div className="space-y-3">
          {resultado.contenido.map((venta) => (
            <div
              key={venta.id}
              className="flex flex-col gap-2 rounded-2xl border border-neutral-200 bg-white p-4 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="font-bold">
                  #{venta.id} · {venta.beatTitulo}
                </p>
                <p className="mt-0.5 text-sm text-neutral-500">
                  Compró {venta.compradorNombre} · {formatearFecha(venta.fecha)}
                </p>
              </div>
              <div className="flex items-center gap-3">
                <span className="font-bold text-green-700">{formatearPrecio(venta.precio)}</span>
                <span
                  className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                    venta.estado === "COMPLETADA"
                      ? "bg-green-100 text-green-800"
                      : "bg-yellow-100 text-yellow-800"
                  }`}
                >
                  {ETIQUETA_ESTADO_COMPRA[venta.estado]}
                </span>
              </div>
            </div>
          ))}
        </div>
        <Pagination pagina={pagina} totalPaginas={resultado.totalPaginas} onCambiar={setPagina} />
      </div>
    </div>
  );
}
