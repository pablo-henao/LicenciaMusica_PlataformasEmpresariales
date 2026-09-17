import { useState, type FormEvent } from "react";
import type { FiltrosCatalogo } from "../../api/beats";

interface FiltrosCatalogoFormProps {
  onBuscar: (filtros: Omit<FiltrosCatalogo, "page" | "size">) => void;
}

export function FiltrosCatalogoForm({ onBuscar }: FiltrosCatalogoFormProps) {
  const [titulo, setTitulo] = useState("");
  const [genero, setGenero] = useState("");
  const [bpmMin, setBpmMin] = useState("");
  const [bpmMax, setBpmMax] = useState("");

  function alEnviar(evento: FormEvent) {
    evento.preventDefault();
    onBuscar({
      titulo: titulo.trim() || undefined,
      genero: genero.trim() || undefined,
      bpmMin: bpmMin ? Number(bpmMin) : undefined,
      bpmMax: bpmMax ? Number(bpmMax) : undefined,
    });
  }

  function limpiar() {
    setTitulo("");
    setGenero("");
    setBpmMin("");
    setBpmMax("");
    onBuscar({});
  }

  return (
    <form onSubmit={alEnviar} className="grid grid-cols-2 gap-3 rounded-lg border border-neutral-200 bg-white p-4 sm:grid-cols-5">
      <div className="col-span-2 sm:col-span-2">
        <label htmlFor="titulo" className="block text-xs font-medium text-neutral-600">
          Título
        </label>
        <input
          id="titulo"
          type="text"
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          placeholder="Ej: Sueños de Medallo"
          className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div>
        <label htmlFor="genero" className="block text-xs font-medium text-neutral-600">
          Género
        </label>
        <input
          id="genero"
          type="text"
          value={genero}
          onChange={(e) => setGenero(e.target.value)}
          placeholder="Ej: Trap"
          className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div>
        <label htmlFor="bpmMin" className="block text-xs font-medium text-neutral-600">
          BPM mín.
        </label>
        <input
          id="bpmMin"
          type="number"
          min={0}
          value={bpmMin}
          onChange={(e) => setBpmMin(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div>
        <label htmlFor="bpmMax" className="block text-xs font-medium text-neutral-600">
          BPM máx.
        </label>
        <input
          id="bpmMax"
          type="number"
          min={0}
          value={bpmMax}
          onChange={(e) => setBpmMax(e.target.value)}
          className="mt-1 w-full rounded-md border border-neutral-300 px-2.5 py-1.5 text-sm focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
        />
      </div>

      <div className="col-span-2 flex items-end gap-2 sm:col-span-5">
        <button
          type="submit"
          className="rounded-md bg-brand-600 px-4 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700"
        >
          Buscar
        </button>
        <button
          type="button"
          onClick={limpiar}
          className="rounded-md border border-neutral-300 px-4 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
        >
          Limpiar
        </button>
      </div>
    </form>
  );
}
