import { useCallback } from "react";
import { Link, useParams } from "react-router-dom";
import { obtenerBeatPorId } from "../../api/beats";
import { Badge } from "../../components/Badge";
import { LicenciasManager } from "../../components/beats/LicenciasManager";
import { ColaboradoresManager } from "../../components/colaboradores/ColaboradoresManager";
import { HistorialCreditos } from "../../components/colaboradores/HistorialCreditos";
import { FormAlert } from "../../components/FormAlert";
import { useApiFetch } from "../../hooks/useApiFetch";

export function GestionarBeatPage() {
  const { id } = useParams<{ id: string }>();
  const beatId = Number(id);

  const fetcher = useCallback(() => obtenerBeatPorId(beatId), [beatId]);
  const { datos: beat, cargando, error } = useApiFetch(fetcher, [beatId], "No se pudo cargar el beat.");

  if (cargando) {
    return <p className="py-10 text-center text-neutral-500">Cargando...</p>;
  }

  if (error || !beat) {
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
    <div className="mx-auto max-w-3xl space-y-8">
      <div>
        <Link to="/mis-beats" className="text-sm font-medium text-brand-600 hover:underline">
          ← Volver a mis beats
        </Link>

        <div className="mt-3 rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-2xl font-bold tracking-tight">{beat.titulo}</h1>
            <Badge variante={beat.estado === "PUBLICADO" ? "success" : "neutral"}>
              {beat.estado === "PUBLICADO" ? "Publicado" : "Borrador"}
            </Badge>
          </div>
          <p className="mt-1 text-sm text-neutral-600">
            {beat.genero} · {beat.bpm} BPM ·{" "}
            <Link to={`/beats/${beat.id}/editar`} className="text-brand-600 hover:underline">
              Editar datos básicos
            </Link>
          </p>
        </div>
      </div>

      <LicenciasManager beatId={beatId} />
      <ColaboradoresManager beatId={beatId} />
      <HistorialCreditos beatId={beatId} />
    </div>
  );
}
