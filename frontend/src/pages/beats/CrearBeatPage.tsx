import { useNavigate } from "react-router-dom";
import { crearBeat, type BeatRequestBody } from "../../api/beats";
import { BeatForm } from "../../components/beats/BeatForm";

export function CrearBeatPage() {
  const navegar = useNavigate();

  async function guardar(datos: BeatRequestBody) {
    await crearBeat(datos);
    navegar("/mis-beats");
  }

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="text-2xl font-bold tracking-tight">Nuevo beat</h1>
      <p className="mt-1 text-sm text-neutral-600">
        Se crea como borrador — puedes definir sus licencias y publicarlo después.
      </p>

      <div className="mt-6">
        <BeatForm onGuardar={guardar} textoBoton="Crear beat" />
      </div>
    </div>
  );
}
