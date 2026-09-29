import { useCallback, useState } from "react";
import { Link } from "react-router-dom";
import { obtenerMisBeats, publicarBeat } from "../../api/beats";
import { ApiError } from "../../api/client";
import type { BeatResponse, PaginaResponse } from "../../api/types";
import { Badge } from "../../components/Badge";
import { FormAlert } from "../../components/FormAlert";
import { Pagination } from "../../components/Pagination";
import { useApiFetch } from "../../hooks/useApiFetch";

const TAMANO_PAGINA = 10;
const PAGINA_VACIA: PaginaResponse<BeatResponse> = {
  contenido: [],
  pagina: 0,
  tamano: TAMANO_PAGINA,
  totalElementos: 0,
  totalPaginas: 0,
};

export function MisBeatsPage() {
  const [pagina, setPagina] = useState(0);
  const [refrescar, setRefrescar] = useState(0);
  const [publicando, setPublicando] = useState<number | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);

  const fetcher = useCallback(() => obtenerMisBeats(pagina, TAMANO_PAGINA), [pagina]);
  const { datos, cargando, error } = useApiFetch(
    fetcher,
    [pagina, refrescar],
    "No se pudieron cargar tus beats.",
  );
  const resultado = datos ?? PAGINA_VACIA;

  async function publicar(beat: BeatResponse) {
    setPublicando(beat.id);
    setErrorAccion(null);

    try {
      await publicarBeat(beat.id);
      setRefrescar((valor) => valor + 1);
    } catch (err) {
      setErrorAccion(
        err instanceof ApiError
          ? err.message
          : "No se pudo publicar el beat.",
      );
    } finally {
      setPublicando(null);
    }
  }

  return (
    <div>
      <div className="flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Mis beats</h1>
          <p className="mt-1 text-sm text-neutral-600">
            Gestiona tus beats, incluidos los que todavía no publicaste.
          </p>
        </div>
        <Link
          to="/beats/nuevo"
          className="rounded-full bg-neutral-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-brand-700"
        >
          + Nuevo beat
        </Link>
      </div>

      <div className="mt-5">
        {(error || errorAccion) && <FormAlert mensaje={error ?? errorAccion ?? ""} />}

        {cargando && <p className="py-10 text-center text-neutral-500">Cargando...</p>}

        {!cargando && resultado.contenido.length === 0 && (
          <div className="rounded-2xl border border-dashed border-neutral-300 bg-white py-12 text-center text-neutral-500">
            Todavía no has creado ningún beat.
          </div>
        )}

        <div className="space-y-3">
          {resultado.contenido.map((beat) => (
            <div
              key={beat.id}
              className="flex flex-col gap-3 rounded-2xl border border-neutral-200 bg-white p-4 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <div className="flex items-center gap-2">
                  <p className="font-bold">{beat.titulo}</p>
                  <Badge variante={beat.estado === "PUBLICADO" ? "success" : "neutral"}>
                    {beat.estado === "PUBLICADO" ? "Publicado" : "Borrador"}
                  </Badge>
                </div>
                <p className="mt-1 text-sm text-neutral-500">
                  {beat.genero} · {beat.bpm} BPM
                </p>
              </div>

              <div className="flex shrink-0 gap-2">
                <Link
                  to={`/mis-beats/${beat.id}`}
                  className="rounded-full border border-neutral-300 px-3.5 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
                >
                  Gestionar
                </Link>
                <Link
                  to={`/beats/${beat.id}/editar`}
                  className="rounded-full border border-neutral-300 px-3.5 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
                >
                  Editar
                </Link>
                {beat.estado === "BORRADOR" && (
                  <button
                    type="button"
                    disabled={publicando === beat.id}
                    onClick={() => publicar(beat)}
                    className="rounded-full bg-brand-600 px-3.5 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
                  >
                    {publicando === beat.id ? "Publicando..." : "Publicar"}
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
