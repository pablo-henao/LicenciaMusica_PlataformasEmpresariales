import { useCallback, useState } from "react";
import { abrirAcuerdo, obtenerAcuerdoDeBeat } from "../../api/acuerdos";
import { ApiError } from "../../api/client";
import { eliminarColaborador, obtenerColaboradoresDeBeat } from "../../api/colaboradores";
import type { AcuerdoCreditosResponse, ColaboradorBeatResponse } from "../../api/types";
import { useApiFetch } from "../../hooks/useApiFetch";
import { ETIQUETA_ESTADO_COLABORADOR, ETIQUETA_ROL_COLABORADOR, VARIANTE_ESTADO_COLABORADOR, formatearFecha } from "../../utils/format";
import { Badge } from "../Badge";
import { FormAlert } from "../FormAlert";
import { EditarColaboradorForm } from "./EditarColaboradorForm";
import { InvitarColaboradorForm } from "./InvitarColaboradorForm";

interface DatosColaboracion {
  acuerdo: AcuerdoCreditosResponse | null;
  colaboradores: ColaboradorBeatResponse[];
}

interface ColaboradoresManagerProps {
  beatId: number;
}

export function ColaboradoresManager({ beatId }: ColaboradoresManagerProps) {
  const [refrescar, setRefrescar] = useState(0);
  const [mostrarInvitar, setMostrarInvitar] = useState(false);
  const [editando, setEditando] = useState<number | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);
  const [abriendoAcuerdo, setAbriendoAcuerdo] = useState(false);

  const fetcher = useCallback(async (): Promise<DatosColaboracion> => {
    const [acuerdo, colaboradores] = await Promise.all([
      obtenerAcuerdoDeBeat(beatId),
      obtenerColaboradoresDeBeat(beatId),
    ]);
    return { acuerdo, colaboradores };
  }, [beatId]);

  const { datos, cargando, error } = useApiFetch(
    fetcher,
    [beatId, refrescar],
    "No se pudo cargar la información de colaboradores.",
  );
  const acuerdo = datos?.acuerdo ?? null;
  const colaboradores = datos?.colaboradores ?? [];
  const sumaPorcentajes = colaboradores.reduce((total, c) => total + c.porcentajePropuesto, 0);

  function refrescarTodo() {
    setMostrarInvitar(false);
    setEditando(null);
    setRefrescar((valor) => valor + 1);
  }

  async function abrir() {
    setAbriendoAcuerdo(true);
    setErrorAccion(null);
    try {
      await abrirAcuerdo(beatId);
      setRefrescar((valor) => valor + 1);
    } catch (err) {
      setErrorAccion(err instanceof ApiError ? err.message : "No se pudo abrir el acuerdo.");
    } finally {
      setAbriendoAcuerdo(false);
    }
  }

  async function eliminar(id: number) {
    if (!window.confirm("¿Quitar a este colaborador? Esto reabre el acuerdo y reinicia las aceptaciones.")) {
      return;
    }

    setErrorAccion(null);
    try {
      await eliminarColaborador(id);
      setRefrescar((valor) => valor + 1);
    } catch (err) {
      setErrorAccion(err instanceof ApiError ? err.message : "No se pudo quitar al colaborador.");
    }
  }

  return (
    <section>
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold">Colaboradores y créditos</h2>
        <button
          type="button"
          onClick={() => {
            setMostrarInvitar((valor) => !valor);
            setEditando(null);
          }}
          className="rounded-full bg-brand-600 px-4 py-1.5 text-sm font-medium text-white transition hover:bg-brand-700"
        >
          {mostrarInvitar ? "Cancelar" : "+ Invitar colaborador"}
        </button>
      </div>

      {error && <div className="mt-3"><FormAlert mensaje={error} /></div>}
      {errorAccion && <div className="mt-3"><FormAlert mensaje={errorAccion} /></div>}

      <div className="mt-3 flex flex-wrap items-center gap-3 rounded-2xl border border-neutral-200 bg-white p-3.5">
        <span className="text-sm font-medium text-neutral-700">Acuerdo de créditos:</span>

        {!acuerdo && (
          <>
            <Badge>Sin abrir</Badge>
            <button
              type="button"
              disabled={abriendoAcuerdo}
              onClick={abrir}
              className="rounded-full border border-brand-300 px-3.5 py-1 text-sm font-medium text-brand-700 transition hover:bg-brand-50 disabled:opacity-50"
            >
              {abriendoAcuerdo ? "Abriendo..." : "Abrir acuerdo"}
            </button>
          </>
        )}

        {acuerdo?.estado === "ABIERTO" && <Badge variante="warning">Abierto</Badge>}

        {acuerdo?.estado === "CERRADO" && (
          <>
            <Badge variante="success">Cerrado</Badge>
            {acuerdo.fechaCierre && (
              <span className="text-xs text-neutral-500">desde {formatearFecha(acuerdo.fechaCierre)}</span>
            )}
          </>
        )}

        {colaboradores.length > 0 && (
          <span
            className={`ml-auto text-sm font-medium ${sumaPorcentajes === 100 ? "text-green-600" : "text-neutral-500"}`}
          >
            Suma de porcentajes: {sumaPorcentajes}%
          </span>
        )}
      </div>

      {mostrarInvitar && (
        <div className="mt-3">
          <InvitarColaboradorForm beatId={beatId} onInvitado={refrescarTodo} />
        </div>
      )}

      {cargando && <p className="mt-3 text-center text-neutral-500">Cargando...</p>}

      {!cargando && colaboradores.length === 0 && !mostrarInvitar && (
        <p className="mt-3 text-sm text-neutral-500">Todavía no invitaste colaboradores a este beat.</p>
      )}

      <div className="mt-3 space-y-2">
        {colaboradores.map((colaborador) =>
          editando === colaborador.id ? (
            <EditarColaboradorForm
              key={colaborador.id}
              beatId={beatId}
              colaborador={colaborador}
              onGuardado={refrescarTodo}
              onCancelar={() => setEditando(null)}
            />
          ) : (
            <div
              key={colaborador.id}
              className="flex flex-col gap-2 rounded-2xl border border-neutral-200 bg-white p-3.5 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <div className="flex items-center gap-2">
                  <p className="font-medium text-neutral-900">{colaborador.usuarioNombre}</p>
                  <Badge variante={VARIANTE_ESTADO_COLABORADOR[colaborador.estado]}>
                    {ETIQUETA_ESTADO_COLABORADOR[colaborador.estado]}
                  </Badge>
                </div>
                <p className="mt-1 text-sm text-neutral-500">
                  {ETIQUETA_ROL_COLABORADOR[colaborador.rol]} · {colaborador.porcentajePropuesto}%
                </p>
              </div>
              <div className="flex shrink-0 gap-2">
                <button
                  type="button"
                  onClick={() => {
                    setEditando(colaborador.id);
                    setMostrarInvitar(false);
                  }}
                  className="rounded-full border border-neutral-300 px-3.5 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100"
                >
                  Editar
                </button>
                <button
                  type="button"
                  onClick={() => eliminar(colaborador.id)}
                  className="rounded-full border border-red-200 px-3.5 py-1.5 text-sm font-medium text-red-700 transition hover:bg-red-50"
                >
                  Quitar
                </button>
              </div>
            </div>
          ),
        )}
      </div>
    </section>
  );
}
