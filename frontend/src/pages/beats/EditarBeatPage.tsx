import { useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { actualizarBeat, obtenerBeatPorId, type BeatRequestBody } from "../../api/beats";
import { BeatForm } from "../../components/beats/BeatForm";
import { FormAlert } from "../../components/FormAlert";
import { useApiFetch } from "../../hooks/useApiFetch";

export function EditarBeatPage() {
  const { id } = useParams<{ id: string }>();
  const beatId = Number(id);
  const navegar = useNavigate();

  const fetcher = useCallback(() => obtenerBeatPorId(beatId), [beatId]);
  const { datos: beat, cargando, error } = useApiFetch(fetcher, [beatId], "No se pudo cargar el beat.");

  async function guardar(datos: BeatRequestBody) {
    await actualizarBeat(beatId, datos);
    navegar("/mis-beats");
  }

  if (cargando) {
    return <p className="py-10 text-center text-neutral-500">Cargando...</p>;
  }

  if (error || !beat) {
    return (
      <div className="mx-auto max-w-md">
        <FormAlert mensaje={error ?? "No se encontró el beat"} />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="text-2xl font-semibold text-neutral-900">Editar beat</h1>
      <p className="mt-1 text-sm text-neutral-600">
        El productor dueño no cambia, y el estado (borrador/publicado) se gestiona desde "Mis beats".
      </p>

      <div className="mt-6">
        <BeatForm
          valoresIniciales={{
            titulo: beat.titulo,
            genero: beat.genero,
            bpm: beat.bpm,
            urlPreview: beat.urlPreview,
          }}
          onGuardar={guardar}
          textoBoton="Guardar cambios"
        />
      </div>
    </div>
  );
}
