import { useCallback, useState } from "react";
import { adminAcuerdos, adminBeats, adminCompras } from "../../api/admin";
import { FormAlert } from "../../components/FormAlert";
import { Pagination } from "../../components/Pagination";
import { useApiFetch } from "../../hooks/useApiFetch";
import {
  ETIQUETA_ESTADO_ACUERDO,
  ETIQUETA_ESTADO_BEAT,
  ETIQUETA_ESTADO_COMPRA,
  formatearFecha,
  formatearPrecio,
} from "../../utils/format";

type Tab = "beats" | "compras" | "acuerdos";
const TAMANO = 12;

export function AdminPage() {
  const [tab, setTab] = useState<Tab>("beats");
  const [pagina, setPagina] = useState(0);

  const fetcherBeats = useCallback(() => adminBeats(pagina, TAMANO), [pagina]);
  const fetcherCompras = useCallback(() => adminCompras(pagina, TAMANO), [pagina]);
  const fetcherAcuerdos = useCallback(() => adminAcuerdos(pagina, TAMANO), [pagina]);

  const beats = useApiFetch(fetcherBeats, [tab, pagina], "No se pudieron cargar los beats.");
  const compras = useApiFetch(fetcherCompras, [tab, pagina], "No se pudieron cargar las compras.");
  const acuerdos = useApiFetch(fetcherAcuerdos, [tab, pagina], "No se pudieron cargar los acuerdos.");

  function cambiarTab(nueva: Tab) {
    setTab(nueva);
    setPagina(0);
  }

  return (
    <div>
      <div className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h1 className="text-2xl font-bold tracking-tight">Panel admin</h1>
        <p className="mt-1 text-sm text-neutral-600">
          Solo lectura: todos los beats (incluidos borradores), compras y acuerdos — para ver
          disputas y reportes.
        </p>
        <div className="mt-3 flex gap-2">
          {(["beats", "compras", "acuerdos"] as const).map((t) => (
            <button
              key={t}
              type="button"
              onClick={() => cambiarTab(t)}
              className={`rounded-full px-4 py-1.5 text-sm font-medium capitalize transition ${
                tab === t ? "bg-neutral-900 text-white" : "border border-neutral-300 text-neutral-600 hover:bg-neutral-100"
              }`}
            >
              {t}
            </button>
          ))}
        </div>
      </div>

      <div className="mt-5">
        {tab === "beats" && (
          <>
            {beats.error && <FormAlert mensaje={beats.error} />}
            <div className="space-y-2">
              {(beats.datos?.contenido ?? []).map((b) => (
                <div key={b.id} className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-neutral-200 bg-white p-3 text-sm">
                  <span className="font-bold">
                    #{b.id} · {b.titulo} · {b.productorNombre}
                  </span>
                  <span className="text-neutral-500">
                    {b.genero} · {b.bpm} BPM · {ETIQUETA_ESTADO_BEAT[b.estado]}
                  </span>
                </div>
              ))}
            </div>
            <Pagination pagina={pagina} totalPaginas={beats.datos?.totalPaginas ?? 0} onCambiar={setPagina} />
          </>
        )}

        {tab === "compras" && (
          <>
            {compras.error && <FormAlert mensaje={compras.error} />}
            <div className="space-y-2">
              {(compras.datos?.contenido ?? []).map((c) => (
                <div key={c.id} className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-neutral-200 bg-white p-3 text-sm">
                  <span className="font-bold">
                    #{c.id} · {c.beatTitulo} · {c.compradorNombre}
                  </span>
                  <span className="text-neutral-500">
                    {formatearPrecio(c.precio)} · {formatearFecha(c.fecha)} · {ETIQUETA_ESTADO_COMPRA[c.estado]}
                  </span>
                </div>
              ))}
            </div>
            <Pagination pagina={pagina} totalPaginas={compras.datos?.totalPaginas ?? 0} onCambiar={setPagina} />
          </>
        )}

        {tab === "acuerdos" && (
          <>
            {acuerdos.error && <FormAlert mensaje={acuerdos.error} />}
            <div className="space-y-2">
              {(acuerdos.datos?.contenido ?? []).map((a) => (
                <div key={a.id} className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-neutral-200 bg-white p-3 text-sm">
                  <span className="font-bold">
                    #{a.id} · Beat #{a.beatId}
                  </span>
                  <span className="text-neutral-500">
                    {ETIQUETA_ESTADO_ACUERDO[a.estado]}
                    {a.fechaCierre ? ` · cierre ${formatearFecha(a.fechaCierre)}` : ""}
                  </span>
                </div>
              ))}
            </div>
            <Pagination pagina={pagina} totalPaginas={acuerdos.datos?.totalPaginas ?? 0} onCambiar={setPagina} />
          </>
        )}
      </div>
    </div>
  );
}
