import { useState, type FormEvent } from "react";
import { ApiError } from "../../api/client";
import type { BeatRequestBody } from "../../api/beats";
import { FormAlert } from "../FormAlert";

interface BeatFormProps {
  valoresIniciales?: {
    titulo: string;
    genero: string;
    bpm: number;
    urlPreview: string | null;
  };
  onGuardar: (valores: BeatRequestBody) => Promise<void>;
  textoBoton: string;
}

export function BeatForm({ valoresIniciales, onGuardar, textoBoton }: BeatFormProps) {
  const [titulo, setTitulo] = useState(valoresIniciales?.titulo ?? "");
  const [genero, setGenero] = useState(valoresIniciales?.genero ?? "");
  const [bpm, setBpm] = useState(valoresIniciales ? String(valoresIniciales.bpm) : "");
  const [urlPreview, setUrlPreview] = useState(valoresIniciales?.urlPreview ?? "");
  const [error, setError] = useState<ApiError | null>(null);
  const [guardando, setGuardando] = useState(false);

  async function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    setError(null);
    setGuardando(true);

    try {
      await onGuardar({
        titulo,
        genero,
        bpm: Number(bpm),
        urlPreview: urlPreview.trim() || null,
      });
    } catch (err) {
      setError(err instanceof ApiError ? err : new ApiError(0, "No se pudo guardar el beat."));
      setGuardando(false);
    }
  }

  return (
    <form onSubmit={alEnviar} className="space-y-4">
      {error && <FormAlert mensaje={error.message} detalles={error.detalles} />}

      <div>
        <label htmlFor="titulo" className="block text-sm font-medium text-neutral-700">
          Título
        </label>
        <input
          id="titulo"
          type="text"
          required
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div>
        <label htmlFor="genero" className="block text-sm font-medium text-neutral-700">
          Género
        </label>
        <input
          id="genero"
          type="text"
          required
          placeholder="Ej: Trap, Reggaetón, Dembow, Bachata"
          value={genero}
          onChange={(e) => setGenero(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div>
        <label htmlFor="bpm" className="block text-sm font-medium text-neutral-700">
          BPM
        </label>
        <input
          id="bpm"
          type="number"
          required
          min={40}
          max={300}
          value={bpm}
          onChange={(e) => setBpm(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div>
        <label htmlFor="urlPreview" className="block text-sm font-medium text-neutral-700">
          URL del preview de audio <span className="font-normal text-neutral-400">(opcional)</span>
        </label>
        <input
          id="urlPreview"
          type="url"
          placeholder="https://..."
          value={urlPreview}
          onChange={(e) => setUrlPreview(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <button
        type="submit"
        disabled={guardando}
        className="rounded-md bg-brand-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
      >
        {guardando ? "Guardando..." : textoBoton}
      </button>
    </form>
  );
}
