import { useCallback, useState } from "react";
import { marcarNotificacionLeida, obtenerMisNotificaciones } from "../../api/usuarios";
import type { NotificacionResponse, PaginaResponse } from "../../api/types";
import { FormAlert } from "../../components/FormAlert";
import { Pagination } from "../../components/Pagination";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_TIPO_NOTIFICACION, formatearFecha } from "../../utils/format";

const TAMANO = 12;
const VACIA: PaginaResponse<NotificacionResponse> = {
  contenido: [],
  pagina: 0,
  tamano: TAMANO,
  totalElementos: 0,
  totalPaginas: 0,
};

export function NotificacionesPage() {
  const [pagina, setPagina] = useState(0);
  const [recargar, setRecargar] = useState(0);

  const fetcher = useCallback(() => obtenerMisNotificaciones(pagina, TAMANO), [pagina]);
  const { datos, cargando, error } = useApiFetch(fetcher, [pagina, recargar], "No se pudieron cargar tus avisos.");
  const resultado = datos ?? VACIA;

  async function marcarLeida(id: number) {
    await marcarNotificacionLeida(id).catch(() => undefined);
    setRecargar((n) => n + 1);
  }

  return (
    <div>
      <div className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h1 className="text-2xl font-bold tracking-tight">Avisos</h1>
        <p className="mt-1 text-sm text-neutral-600">
          Invitaciones, aceptaciones, rechazos y compras — todo te llega aquí.
        </p>
      </div>

      <div className="mt-5">
        {error && <FormAlert mensaje={error} />}
        {cargando && <p className="py-10 text-center text-neutral-500">Cargando...</p>}
        {!cargando && !error && resultado.contenido.length === 0 && (
          <div className="rounded-2xl border border-dashed border-neutral-300 bg-white py-12 text-center text-neutral-500">
            No tienes avisos todavía.
          </div>
        )}
        <div className="space-y-3">
          {resultado.contenido.map((n) => (
            <div
              key={n.id}
              className={`flex flex-col gap-2 rounded-2xl border p-4 sm:flex-row sm:items-center sm:justify-between ${
                n.leida ? "border-neutral-200 bg-white" : "border-brand-300 bg-brand-50"
              }`}
            >
              <div>
                <p className="flex flex-wrap items-center gap-2 text-sm">
                  <span className="rounded-full bg-neutral-900 px-2 py-0.5 text-xs font-bold text-white">
                    {ETIQUETA_TIPO_NOTIFICACION[n.tipo] ?? n.tipo}
                  </span>
                  {!n.leida && (
                    <span className="rounded-full bg-brand-600 px-2 py-0.5 text-xs font-bold text-white">
                      Nuevo
                    </span>
                  )}
                </p>
                <p className="mt-1 text-sm">{n.mensaje}</p>
                <p className="mt-0.5 text-xs text-neutral-400">{formatearFecha(n.fecha)}</p>
              </div>
              {!n.leida && (
                <button
                  type="button"
                  onClick={() => marcarLeida(n.id)}
                  className="shrink-0 rounded-full border border-neutral-300 px-4 py-1.5 text-sm text-neutral-600 hover:bg-neutral-100"
                >
                  Marcar leída
                </button>
              )}
            </div>
          ))}
        </div>
        <Pagination pagina={pagina} totalPaginas={resultado.totalPaginas} onCambiar={setPagina} />
      </div>
    </div>
  );
}
