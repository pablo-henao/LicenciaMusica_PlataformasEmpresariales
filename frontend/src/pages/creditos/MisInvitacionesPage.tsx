import { useCallback, useState } from "react";
import { Link } from "react-router-dom";
import {
  aceptarColaboracion,
  obtenerMisInvitaciones,
  rechazarColaboracion,
} from "../../api/creditos";
import type { ColaboradorBeatResponse, EstadoColaborador } from "../../api/types";
import { ApiError } from "../../api/client";
import { FormAlert } from "../../components/FormAlert";
import { useApiFetch } from "../../hooks/useApiFetch";
import {
  ETIQUETA_ESTADO_COLABORADOR,
  ETIQUETA_ROL_COLABORADOR,
} from "../../utils/format";

export function MisInvitacionesPage() {
  const [filtro, setFiltro] = useState<EstadoColaborador | "TODAS">("PENDIENTE");
  const [recargar, setRecargar] = useState(0);
  const [trabajando, setTrabajando] = useState<number | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);

  const fetcher = useCallback(
    () => obtenerMisInvitaciones(filtro === "TODAS" ? undefined : filtro),
    [filtro],
  );
  const { datos, cargando, error } = useApiFetch<ColaboradorBeatResponse[]>(
    fetcher,
    [filtro, recargar],
    "No se pudieron cargar tus invitaciones.",
  );
  const lista = datos ?? [];

  async function responder(id: number, accion: "aceptar" | "rechazar") {
    setTrabajando(id);
    setErrorAccion(null);
    try {
      if (accion === "aceptar") {
        await aceptarColaboracion(id);
      } else {
        await rechazarColaboracion(id);
      }
      setRecargar((n) => n + 1);
    } catch (e) {
      setErrorAccion(e instanceof ApiError ? e.message : "No se pudo responder la invitación.");
    } finally {
      setTrabajando(null);
    }
  }

  return (
    <div>
      <div className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h1 className="text-2xl font-bold tracking-tight">Mis splits</h1>
        <p className="mt-1 text-sm text-neutral-600">
          Invitaciones a colaborar en beats de otros productores. Acepta o rechaza tu porcentaje.
        </p>
        <div className="mt-3 flex gap-2">
          {(["PENDIENTE", "ACEPTADO", "RECHAZADO", "TODAS"] as const).map((op) => (
            <button
              key={op}
              type="button"
              onClick={() => setFiltro(op)}
              className={`rounded-full px-3.5 py-1.5 text-sm font-medium transition ${
                filtro === op
                  ? "bg-neutral-900 text-white"
                  : "border border-neutral-300 text-neutral-600 hover:bg-neutral-100"
              }`}
            >
              {op === "TODAS" ? "Todas" : ETIQUETA_ESTADO_COLABORADOR[op]}
            </button>
          ))}
        </div>
      </div>

      <div className="mt-5">
        {error && <FormAlert mensaje={error} />}
        {errorAccion && (
          <div className="mb-3">
            <FormAlert mensaje={errorAccion} />
          </div>
        )}
        {cargando && <p className="py-10 text-center text-neutral-500">Cargando...</p>}
        {!cargando && !error && lista.length === 0 && (
          <div className="rounded-2xl border border-dashed border-neutral-300 bg-white py-12 text-center text-neutral-500">
            No tienes invitaciones en este estado.
          </div>
        )}
        <div className="space-y-3">
          {lista.map((inv) => (
            <div
              key={inv.id}
              className="flex flex-col gap-3 rounded-2xl border border-neutral-200 bg-white p-4 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="font-bold">
                  Beat #{inv.beatId} · {ETIQUETA_ROL_COLABORADOR[inv.rol]} · {inv.porcentajePropuesto}%
                </p>
                <p className="mt-0.5 text-sm text-neutral-500">
                  Invitado: {inv.usuarioNombre} ·{" "}
                  <span className="font-semibold">{ETIQUETA_ESTADO_COLABORADOR[inv.estado]}</span> ·{" "}
                  <Link to={`/beats/${inv.beatId}`} className="text-brand-600 hover:underline">
                    Ver beat
                  </Link>
                </p>
              </div>
              {inv.estado === "PENDIENTE" && (
                <div className="flex gap-2">
                  <button
                    type="button"
                    disabled={trabajando === inv.id}
                    onClick={() => responder(inv.id, "aceptar")}
                    className="rounded-full bg-green-600 px-4 py-1.5 text-sm font-medium text-white hover:bg-green-700 disabled:opacity-50"
                  >
                    Aceptar
                  </button>
                  <button
                    type="button"
                    disabled={trabajando === inv.id}
                    onClick={() => responder(inv.id, "rechazar")}
                    className="rounded-full border border-red-200 px-4 py-1.5 text-sm font-medium text-red-600 hover:bg-red-50 disabled:opacity-50"
                  >
                    Rechazar
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
