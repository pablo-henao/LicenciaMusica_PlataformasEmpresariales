import { useCallback } from "react";
import { Link, useParams } from "react-router-dom";
import { obtenerBeatPorId } from "../../api/beats";
import { obtenerLicenciasDeBeat } from "../../api/licencias";
import type { BeatResponse, TipoLicenciaResponse } from "../../api/types";
import { FormAlert } from "../../components/FormAlert";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_TIPO_LICENCIA, formatearPrecio } from "../../utils/format";

interface DetalleBeat {
  beat: BeatResponse;
  licencias: TipoLicenciaResponse[];
}

export function BeatDetailPage() {
  const { id } = useParams<{ id: string }>();
  const beatId = Number(id);

  const fetcher = useCallback(async (): Promise<DetalleBeat> => {
    const [beat, licencias] = await Promise.all([
      obtenerBeatPorId(beatId),
      obtenerLicenciasDeBeat(beatId),
    ]);
    return { beat, licencias };
  }, [beatId]);

  const { datos, cargando, error } = useApiFetch(
    fetcher,
    [beatId],
    "No se encontró el beat, o ya no está disponible.",
  );

  if (cargando) {
    return <p className="py-10 text-center text-neutral-500">Cargando...</p>;
  }

  if (error || !datos) {
    return (
      <div className="mx-auto max-w-md">
        <FormAlert mensaje={error ?? "No se encontró el beat"} />
        <Link to="/catalogo" className="mt-4 inline-block text-sm font-medium text-brand-600 hover:underline">
          ← Volver al catálogo
        </Link>
      </div>
    );
  }

  const { beat, licencias } = datos;

  return (
    <div className="mx-auto max-w-3xl">
      <Link to="/catalogo" className="text-sm font-medium text-brand-600 hover:underline">
        ← Volver al catálogo
      </Link>

      <div className="mt-4 rounded-lg border border-neutral-200 bg-white p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-2xl font-semibold text-neutral-900">{beat.titulo}</h1>
            <p className="mt-1 text-sm text-neutral-600">Por {beat.productorNombre}</p>
          </div>
          <span className="rounded-full bg-brand-50 px-3 py-1 text-sm font-medium text-brand-700">
            {beat.genero}
          </span>
        </div>

        <p className="mt-2 text-sm text-neutral-500">{beat.bpm} BPM</p>

        {beat.urlPreview && (
          <audio controls preload="none" src={beat.urlPreview} className="mt-4 w-full" />
        )}
      </div>

      <div className="mt-8">
        <h2 className="text-lg font-semibold text-neutral-900">Licencias disponibles</h2>

        {licencias.length === 0 && (
          <p className="mt-2 text-sm text-neutral-500">
            El productor todavía no definió licencias para este beat.
          </p>
        )}

        <div className="mt-3 space-y-3">
          {licencias.map((licencia) => (
            <div
              key={licencia.id}
              className="flex flex-col gap-2 rounded-lg border border-neutral-200 bg-white p-4 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="font-medium text-neutral-900">{ETIQUETA_TIPO_LICENCIA[licencia.tipo]}</p>
                {licencia.condiciones && (
                  <p className="mt-1 text-sm text-neutral-600">{licencia.condiciones}</p>
                )}
              </div>
              <p className="shrink-0 text-lg font-semibold text-brand-700">
                {formatearPrecio(licencia.precio)}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
