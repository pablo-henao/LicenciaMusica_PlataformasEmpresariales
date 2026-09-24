import { useCallback, useState } from "react";
import { Link } from "react-router-dom";
import {
  actualizarBeat,
  crearBeat,
  eliminarBeat,
  obtenerMisBeats,
  publicarBeat,
} from "../../api/beats";
import type { BeatRequest, BeatResponse, PaginaResponse } from "../../api/types";
import { ApiError } from "../../api/client";
import { FormAlert } from "../../components/FormAlert";
import { Pagination } from "../../components/Pagination";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_ESTADO_BEAT } from "../../utils/format";

const TAMANO = 12;
const VACIA: PaginaResponse<BeatResponse> = {
  contenido: [],
  pagina: 0,
  tamano: TAMANO,
  totalElementos: 0,
  totalPaginas: 0,
};

const GENEROS = ["Trap", "Reggaetón", "Dembow", "Bachata", "Drill", "Hip-Hop"];

export function MisBeatsPage() {
  const [pagina, setPagina] = useState(0);
  const [recargar, setRecargar] = useState(0);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);
  const [editando, setEditando] = useState<BeatResponse | null>(null);
  const [creando, setCreando] = useState(false);

  const fetcher = useCallback(() => obtenerMisBeats(pagina, TAMANO), [pagina]);
  const { datos, cargando, error } = useApiFetch(fetcher, [pagina, recargar], "No se pudieron cargar tus beats.");
  const resultado = datos ?? VACIA;

  function recargarLista() {
    setRecargar((n) => n + 1);
    setEditando(null);
    setCreando(false);
  }

  async function publicar(id: number) {
    setErrorAccion(null);
    try {
      await publicarBeat(id);
      recargarLista();
    } catch (e) {
      setErrorAccion(
        e instanceof ApiError
          ? e.message
          : "No se pudo publicar. Revisa que el split sume 100% y esté aceptado por todos.",
      );
    }
  }

  async function eliminar(id: number, titulo: string) {
    if (!confirm(`¿Eliminar "${titulo}"?`)) return;
    setErrorAccion(null);
    try {
      await eliminarBeat(id);
      recargarLista();
    } catch (e) {
      setErrorAccion(e instanceof ApiError ? e.message : "No se pudo eliminar el beat.");
    }
  }

  return (
    <div>
      <div className="flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Mis beats</h1>
          <p className="mt-1 text-sm text-neutral-600">
            Crea borradores, define licencias, declara splits y publica cuando esté listo.
          </p>
        </div>
        <button
          type="button"
          onClick={() => {
            setCreando(true);
            setEditando(null);
          }}
          className="rounded-full bg-neutral-900 px-5 py-2 text-sm font-medium text-white hover:bg-brand-700"
        >
          + Nuevo beat
        </button>
      </div>

      {(creando || editando) && (
        <div className="mt-4">
          <BeatForm
            inicial={editando}
            onCancelar={() => {
              setCreando(false);
              setEditando(null);
            }}
            onGuardado={recargarLista}
          />
        </div>
      )}

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
            Aún no tienes beats. Crea tu primer borrador.
          </div>
        )}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {resultado.contenido.map((beat) => (
            <div key={beat.id} className="rounded-2xl border border-neutral-200 bg-white p-4">
              <div className="flex items-start justify-between gap-2">
                <div className="min-w-0">
                  <p className="truncate font-bold">{beat.titulo}</p>
                  <p className="text-sm text-neutral-500">
                    {beat.genero} · {beat.bpm} BPM
                  </p>
                </div>
                <span
                  className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold ${
                    beat.estado === "PUBLICADO"
                      ? "bg-green-100 text-green-800"
                      : "bg-yellow-100 text-yellow-800"
                  }`}
                >
                  {ETIQUETA_ESTADO_BEAT[beat.estado]}
                </span>
              </div>
              {beat.urlPreview && (
                <audio controls preload="none" src={beat.urlPreview} className="mt-3 h-9 w-full" />
              )}
              <div className="mt-3 flex flex-wrap gap-2">
                <Link
                  to={`/mis-beats/${beat.id}`}
                  className="rounded-full bg-neutral-900 px-3.5 py-1.5 text-xs font-medium text-white hover:bg-brand-700"
                >
                  Gestionar
                </Link>
                {beat.estado === "BORRADOR" && (
                  <button
                    type="button"
                    onClick={() => publicar(beat.id)}
                    className="rounded-full border border-green-300 px-3.5 py-1.5 text-xs font-medium text-green-700 hover:bg-green-50"
                  >
                    Publicar
                  </button>
                )}
                <button
                  type="button"
                  onClick={() => {
                    setEditando(beat);
                    setCreando(false);
                  }}
                  className="rounded-full border border-neutral-300 px-3.5 py-1.5 text-xs font-medium text-neutral-600 hover:bg-neutral-100"
                >
                  Editar
                </button>
                <button
                  type="button"
                  onClick={() => eliminar(beat.id, beat.titulo)}
                  className="rounded-full border border-red-200 px-3.5 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50"
                >
                  Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>
        <Pagination pagina={pagina} totalPaginas={resultado.totalPaginas} onCambiar={setPagina} />
      </div>
    </div>
  );
}

function BeatForm({
  inicial,
  onCancelar,
  onGuardado,
}: {
  inicial: BeatResponse | null;
  onCancelar: () => void;
  onGuardado: () => void;
}) {
  const [titulo, setTitulo] = useState(inicial?.titulo ?? "");
  const [genero, setGenero] = useState(inicial?.genero ?? "Trap");
  const [bpm, setBpm] = useState(String(inicial?.bpm ?? 140));
  const [urlPreview, setUrlPreview] = useState(inicial?.urlPreview ?? "");
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function guardar(evento: React.FormEvent) {
    evento.preventDefault();
    setGuardando(true);
    setError(null);
    const datos: BeatRequest = {
      titulo: titulo.trim(),
      genero: genero.trim(),
      bpm: Number(bpm),
      urlPreview: urlPreview.trim() || undefined,
    };
    try {
      if (inicial) {
        await actualizarBeat(inicial.id, datos);
      } else {
        await crearBeat(datos);
      }
      onGuardado();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "No se pudo guardar el beat.");
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form onSubmit={guardar} className="rounded-2xl border border-brand-200 bg-brand-50 p-5">
      <h2 className="font-bold">{inicial ? `Editar "${inicial.titulo}"` : "Nuevo beat (borrador)"}</h2>
      {error && (
        <div className="mt-3">
          <FormAlert mensaje={error} />
        </div>
      )}
      <div className="mt-3 grid gap-3 sm:grid-cols-2">
        <div>
          <label className="block text-xs font-medium text-neutral-600">Título *</label>
          <input
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
            required
            placeholder="Ej: Perreo Galáctico"
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm outline-none focus:border-brand-500"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-neutral-600">Género *</label>
          <select
            value={genero}
            onChange={(e) => setGenero(e.target.value)}
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm outline-none focus:border-brand-500"
          >
            {GENEROS.map((g) => (
              <option key={g} value={g}>
                {g}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-neutral-600">BPM (40–300) *</label>
          <input
            type="number"
            min={40}
            max={300}
            value={bpm}
            onChange={(e) => setBpm(e.target.value)}
            required
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm outline-none focus:border-brand-500"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-neutral-600">URL preview (mp3)</label>
          <input
            value={urlPreview}
            onChange={(e) => setUrlPreview(e.target.value)}
            placeholder="https://..."
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm outline-none focus:border-brand-500"
          />
        </div>
      </div>
      <div className="mt-4 flex gap-2">
        <button
          type="submit"
          disabled={guardando}
          className="rounded-full bg-neutral-900 px-5 py-2 text-sm font-medium text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {guardando ? "Guardando..." : inicial ? "Guardar cambios" : "Crear borrador"}
        </button>
        <button
          type="button"
          onClick={onCancelar}
          className="rounded-full border border-neutral-300 bg-white px-5 py-2 text-sm font-medium text-neutral-600 hover:bg-neutral-100"
        >
          Cancelar
        </button>
      </div>
    </form>
  );
}
