import { useCallback, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { obtenerBeatPorId } from "../../api/beats";
import { obtenerLicenciasDeBeat } from "../../api/licencias";
import { obtenerAcuerdos, obtenerHistorialCreditos } from "../../api/creditos";
import { api } from "../../api/client";
import type {
  AcuerdoCreditosEventoResponse,
  AcuerdoCreditosResponse,
  BeatResponse,
  ColaboradorBeatResponse,
  TipoLicenciaResponse,
} from "../../api/types";
import { FormAlert } from "../../components/FormAlert";
import { LicenciasSection } from "../../components/beats/LicenciasSection";
import { ColaboradoresSection } from "../../components/beats/ColaboradoresSection";
import { HistorialSection } from "../../components/beats/HistorialSection";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_ESTADO_BEAT } from "../../utils/format";

interface Gestion {
  beat: BeatResponse;
  licencias: TipoLicenciaResponse[];
  colaboradores: ColaboradorBeatResponse[];
  acuerdo: AcuerdoCreditosResponse | null;
  historial: AcuerdoCreditosEventoResponse[];
}

export function BeatGestionPage() {
  const { id } = useParams<{ id: string }>();
  const beatId = Number(id);
  const [recargar, setRecargar] = useState(0);

  const fetcher = useCallback(async (): Promise<Gestion> => {
    const [beat, licencias, acuerdos, historial] = await Promise.all([
      obtenerBeatPorId(beatId),
      obtenerLicenciasDeBeat(beatId),
      obtenerAcuerdos(),
      obtenerHistorialCreditos(beatId).catch(() => [] as AcuerdoCreditosEventoResponse[]),
    ]);
    // Colaboradores visibles de este beat (el endpoint lista lo visible; se filtra por beat).
    const todos = await api
      .get<ColaboradorBeatResponse[]>("/api/colaboradores")
      .catch(() => [] as ColaboradorBeatResponse[]);
    return {
      beat,
      licencias,
      colaboradores: todos.filter((c) => c.beatId === beatId),
      acuerdo: acuerdos.find((a) => a.beatId === beatId) ?? null,
      historial,
    };
  }, [beatId]);

  const { datos, cargando, error } = useApiFetch(
    fetcher,
    [beatId, recargar],
    "No se pudo cargar la gestión del beat.",
  );

  function alCambiar() {
    setRecargar((n) => n + 1);
  }

  if (cargando) return <p className="py-10 text-center text-neutral-500">Cargando...</p>;
  if (error || !datos) {
    return (
      <div className="mx-auto max-w-md">
        <FormAlert mensaje={error ?? "No se encontró el beat"} />
        <Link to="/mis-beats" className="mt-4 inline-block text-sm font-medium text-brand-600 hover:underline">
          ← Volver a mis beats
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <Link to="/mis-beats" className="text-sm font-medium text-brand-600 hover:underline">
        ← Volver a mis beats
      </Link>

      <div className="rounded-2xl border border-neutral-200 bg-white p-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h1 className="text-xl font-bold">
              {datos.beat.titulo} · {datos.beat.genero} · {datos.beat.bpm} BPM
            </h1>
            <p className="text-sm text-neutral-500">
              Gestiona licencias, splits e historial.{" "}
              <Link to={`/beats/${datos.beat.id}`} className="font-medium text-brand-600 hover:underline">
                Ver como comprador →
              </Link>
            </p>
          </div>
          <span
            className={`rounded-full px-3 py-1 text-xs font-bold ${
              datos.beat.estado === "PUBLICADO"
                ? "bg-green-100 text-green-800"
                : "bg-yellow-100 text-yellow-800"
            }`}
          >
            {ETIQUETA_ESTADO_BEAT[datos.beat.estado]}
          </span>
        </div>
        {datos.beat.urlPreview && (
          <audio controls preload="none" src={datos.beat.urlPreview} className="mt-3 h-9 w-full" />
        )}
      </div>

      <LicenciasSection beatId={beatId} licencias={datos.licencias} onCambio={alCambiar} />
      <ColaboradoresSection
        beatId={beatId}
        colaboradores={datos.colaboradores}
        acuerdo={datos.acuerdo}
        onCambio={alCambiar}
      />
      <HistorialSection eventos={datos.historial} />
    </div>
  );
}
