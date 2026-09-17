import { Link } from "react-router-dom";
import type { BeatResponse } from "../../api/types";

interface BeatCardProps {
  beat: BeatResponse;
}

export function BeatCard({ beat }: BeatCardProps) {
  return (
    <div className="flex flex-col rounded-lg border border-neutral-200 bg-white p-4 shadow-sm transition hover:border-brand-300 hover:shadow-md">
      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0">
          <Link
            to={`/beats/${beat.id}`}
            className="block truncate font-semibold text-neutral-900 hover:text-brand-700"
          >
            {beat.titulo}
          </Link>
          <p className="truncate text-sm text-neutral-500">{beat.productorNombre}</p>
        </div>
        <span className="shrink-0 rounded-full bg-brand-50 px-2 py-0.5 text-xs font-medium text-brand-700">
          {beat.genero}
        </span>
      </div>

      <p className="mt-1 text-xs text-neutral-500">{beat.bpm} BPM</p>

      {beat.urlPreview && (
        <audio controls preload="none" src={beat.urlPreview} className="mt-3 h-8 w-full" />
      )}

      <Link
        to={`/beats/${beat.id}`}
        className="mt-3 text-sm font-medium text-brand-600 hover:underline"
      >
        Ver detalle →
      </Link>
    </div>
  );
}
