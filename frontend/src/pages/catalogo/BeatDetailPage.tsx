import { useCallback, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { obtenerBeatPorId } from "../../api/beats";
import { obtenerLicenciasDeBeat } from "../../api/licencias";
import { crearCompra } from "../../api/compras";
import { ApiError } from "../../api/client";
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

  const [comprando, setComprando] = useState<number | null>(null);
  const [errorCompra, setErrorCompra] = useState<string | null>(null);
  const [licenciaComprada, setLicenciaComprada] = useState<number | null>(null);

  async function comprar(tipoLicenciaId: number) {
    setComprando(tipoLicenciaId);
    setErrorCompra(null);

    try {
      await crearCompra({ tipoLicenciaId });
      setLicenciaComprada(tipoLicenciaId);
    } catch (err) {
      setErrorCompra(err instanceof ApiError ? err.message : "No se pudo iniciar la compra.");
    } finally {
      setComprando(null);
    }
  }

  if (cargando) {
    return <p className="py-10 text-center text-neutral-500">Cargando...</p>;
  }

  if (error || !datos) {
    return (
      <div className="mx-auto max-w-md">
        <FormAlert mensaje={error ?? "No se encontró el beat"} />
        <Link to="/catalogo" className="mt-4 inline-block text-sm font-medium text-brand-600 hover:underline">
          ← Volver a la tienda
        </Link>
      </div>
    );
  }

  const { beat, licencias } = datos;

  return (
    <div className="mx-auto max-w-3xl">
      <Link to="/catalogo" className="text-sm font-medium text-brand-600 hover:underline">
        ← Volver a la tienda
      </Link>

      <div className="mt-4 overflow-hidden rounded-2xl border border-neutral-200 bg-white">
        <div className="hero-tide relative px-6 py-8 text-white">
          <div className="hero-grid absolute inset-0" />
          <div className="relative flex flex-wrap items-start justify-between gap-3">
            <div>
              <p className="text-[11px] font-medium tracking-[0.2em] text-white/60 uppercase">
                Detalle del beat
              </p>
              <h1 className="font-display mt-1 text-3xl font-black uppercase sm:text-4xl">
                {beat.titulo}
              </h1>
              <p className="mt-1 text-sm text-white/70">Por {beat.productorNombre}</p>
            </div>
            <span className="rounded-full bg-white/15 px-3 py-1 text-sm font-medium backdrop-blur">
              {beat.genero}
            </span>
          </div>
          <p className="relative mt-2 text-sm text-white/60">{beat.bpm} BPM</p>
          {beat.urlPreview && (
            <audio controls preload="none" src={beat.urlPreview} className="relative mt-4 w-full" />
          )}
        </div>

        <div className="p-6">
          <h2 className="text-lg font-bold">Licencias disponibles</h2>
          <p className="mt-1 text-sm text-neutral-500">
            Cada compra genera un contrato en PDF con los términos aceptados.
          </p>

          {errorCompra && (
            <div className="mt-3">
              <FormAlert mensaje={errorCompra} />
            </div>
          )}

          {licencias.length === 0 && (
            <p className="mt-3 rounded-xl bg-neutral-100 p-4 text-sm text-neutral-500">
              El productor todavía no definió licencias para este beat.
            </p>
          )}

          <div className="mt-3 space-y-3">
            {licencias.map((licencia) => (
              <div
                key={licencia.id}
                className={`flex flex-col gap-3 rounded-2xl border p-4 sm:flex-row sm:items-center sm:justify-between ${
                  licenciaComprada === licencia.id
                    ? "border-brand-500 bg-brand-50"
                    : "border-neutral-200 bg-white"
                }`}
              >
                <div>
                  <p className="font-bold">{ETIQUETA_TIPO_LICENCIA[licencia.tipo]}</p>
                  {licencia.condiciones && (
                    <p className="mt-1 text-sm text-neutral-600">{licencia.condiciones}</p>
                  )}
                  <p className="mt-1 text-lg font-bold text-brand-700">
                    {formatearPrecio(licencia.precio)}
                  </p>
                </div>

                {licenciaComprada === licencia.id ? (
                  <Link
                    to="/mis-compras"
                    className="shrink-0 rounded-full bg-green-600 px-5 py-2 text-center text-sm font-medium text-white transition hover:bg-green-700"
                  >
                    Ir a mis compras →
                  </Link>
                ) : (
                  <button
                    type="button"
                    disabled={comprando === licencia.id}
                    onClick={() => comprar(licencia.id)}
                    className="shrink-0 rounded-full bg-neutral-900 px-5 py-2 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
                  >
                    {comprando === licencia.id ? "Iniciando..." : "Comprar"}
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
