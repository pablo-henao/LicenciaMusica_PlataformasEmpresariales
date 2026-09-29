import { Link } from "react-router-dom";
import { formatearPrecio } from "../../utils/format";
import type { BeatResponse } from "../../api/types";

interface BeatCardProps {
  beat: BeatResponse;
  desdePrecio?: number | null;
}

export function BeatCard({ beat, desdePrecio }: BeatCardProps) {
  return (
    <div className="flex flex-col rounded-2xl border border-neutral-200 bg-white p-4 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <div className="flex items-center gap-3">
        <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-neutral-900 to-brand-800 text-xl text-white">
          ♪
        </div>
        <div className="min-w-0 flex-1">
          <Link
            to={`/beats/${beat.id}`}
            className="block truncate font-bold text-neutral-900 hover:text-brand-700"
          >
            {beat.titulo}
          </Link>
          <p className="truncate text-sm text-neutral-500">{beat.productorNombre}</p>
        </div>
        <span className="shrink-0 rounded-full bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700">
          {beat.genero}
        </span>
      </div>

      <div className="mt-2 flex items-center justify-between text-xs text-neutral-500">
        <span>{beat.bpm} BPM</span>
        {desdePrecio != null && (
          <span className="font-bold text-neutral-900">{formatearPrecio(desdePrecio)}</span>
        )}
      </div>

      {beat.urlPreview && (
        <audio controls preload="none" src={beat.urlPreview} className="mt-3 h-9 w-full" />
      )}

      <Link
        to={`/beats/${beat.id}`}
        className="mt-3 rounded-full bg-neutral-900 px-4 py-2 text-center text-sm font-medium text-white transition hover:bg-brand-700"
      >
        Ver detalle →
      </Link>
    </div>
  );
}
