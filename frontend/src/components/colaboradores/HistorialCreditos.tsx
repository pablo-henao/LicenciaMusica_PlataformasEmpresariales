import { useCallback } from "react";
import { obtenerHistorialCreditos } from "../../api/acuerdos";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_EVENTO_ACUERDO, formatearFecha } from "../../utils/format";
import { FormAlert } from "../FormAlert";

interface HistorialCreditosProps {
  beatId: number;
}

export function HistorialCreditos({ beatId }: HistorialCreditosProps) {
  const fetcher = useCallback(() => obtenerHistorialCreditos(beatId), [beatId]);
  const { datos, cargando, error } = useApiFetch(fetcher, [beatId], "No se pudo cargar el historial.");
  const eventos = datos ?? [];

  return (
    <section>
      <h2 className="text-lg font-semibold text-neutral-900">Historial de créditos</h2>
      <p className="mt-1 text-sm text-neutral-600">Quién propuso, aceptó, rechazó o modificó el acuerdo, en orden.</p>

      {error && <div className="mt-3"><FormAlert mensaje={error} /></div>}

      {cargando && <p className="mt-3 text-center text-neutral-500">Cargando...</p>}

      {!cargando && eventos.length === 0 && (
        <p className="mt-3 text-sm text-neutral-500">Todavía no hay eventos registrados.</p>
      )}

      {eventos.length > 0 && (
        <ol className="mt-4 space-y-4 border-l-2 border-neutral-200 pl-4">
          {eventos.map((evento) => (
            <li key={evento.id}>
              <p className="text-sm font-medium text-neutral-900">
                {ETIQUETA_EVENTO_ACUERDO[evento.tipo]}{" "}
                <span className="font-normal text-neutral-500">— {evento.usuarioNombre}</span>
              </p>
              {evento.detalle && <p className="mt-0.5 text-sm text-neutral-600">{evento.detalle}</p>}
              <p className="mt-0.5 text-xs text-neutral-400">{formatearFecha(evento.fecha)}</p>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}
