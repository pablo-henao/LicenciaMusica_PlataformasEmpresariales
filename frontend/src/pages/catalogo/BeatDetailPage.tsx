import { useCallback, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { obtenerBeatPorId } from "../../api/beats";
import { obtenerLicenciasDeBeat } from "../../api/licencias";
import { checkoutCompra, crearCompra, descargarContrato } from "../../api/compras";
import { ApiError } from "../../api/client";
import type { BeatResponse, CompraResponse, TipoLicenciaResponse } from "../../api/types";
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
  const [licenciaElegida, setLicenciaElegida] = useState<number | null>(null);
  const [compra, setCompra] = useState<CompraResponse | null>(null);
  const [accionando, setAccionando] = useState(false);
  const [mensaje, setMensaje] = useState<string | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);

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

  async function comprar(licencia: TipoLicenciaResponse) {
    setAccionando(true);
    setErrorAccion(null);
    setMensaje(null);
    try {
      const nueva = await crearCompra({ tipoLicenciaId: licencia.id });
      const completada = await checkoutCompra(nueva.id);
      setCompra(completada);
      setMensaje("Compra completada. Ya puedes descargar tu contrato en PDF.");
    } catch (e) {
      setErrorAccion(e instanceof ApiError ? e.message : "No se pudo completar la compra.");
    } finally {
      setAccionando(false);
    }
  }

  async function descargar() {
    if (!compra) return;
    try {
      await descargarContrato(compra);
    } catch {
      setErrorAccion("No se pudo descargar el contrato.");
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
          ← Volver al catálogo
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
                01 · Tabla de surf → Beat
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
            Cada compra genera su contrato en PDF con los términos aceptados.
          </p>

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
                  licenciaElegida === licencia.id
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
                <button
                  type="button"
                  disabled={accionando}
                  onClick={() => {
                    setLicenciaElegida(licencia.id);
                    comprar(licencia);
                  }}
                  className="shrink-0 rounded-full bg-neutral-900 px-5 py-2 text-sm font-medium text-white transition hover:bg-brand-700 disabled:opacity-50"
                >
                  {accionando && licenciaElegida === licencia.id ? "Procesando..." : "Comprar ahora"}
                </button>
              </div>
            ))}
          </div>

          {mensaje && (
            <div className="mt-4 rounded-xl border border-green-200 bg-green-50 p-4 text-sm text-green-800">
              {mensaje}
              {compra?.contratoDisponible && (
                <button
                  type="button"
                  onClick={descargar}
                  className="ml-3 rounded-full bg-green-700 px-4 py-1.5 text-xs font-semibold text-white hover:bg-green-800"
                >
                  Descargar contrato PDF
                </button>
              )}
            </div>
          )}
          {errorAccion && (
            <div className="mt-4">
              <FormAlert mensaje={errorAccion} />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
