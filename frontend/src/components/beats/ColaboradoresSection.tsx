import { useState } from "react";
import {
  abrirAcuerdo,
  actualizarColaborador,
  eliminarAcuerdo,
  eliminarColaborador,
  invitarColaborador,
} from "../../api/creditos";
import { buscarUsuarioPorEmail } from "../../api/usuarios";
import { ApiError } from "../../api/client";
import type {
  AcuerdoCreditosResponse,
  ColaboradorBeatRequest,
  ColaboradorBeatResponse,
  RolColaborador,
} from "../../api/types";
import { FormAlert } from "../../components/FormAlert";
import {
  ETIQUETA_ESTADO_ACUERDO,
  ETIQUETA_ESTADO_COLABORADOR,
  ETIQUETA_ROL_COLABORADOR,
} from "../../utils/format";

const ROLES: RolColaborador[] = ["PRODUCTOR", "CO_PRODUCTOR", "VOCALISTA", "MEZCLA", "MASTERING"];

export function ColaboradoresSection({
  beatId,
  colaboradores,
  acuerdo,
  onCambio,
}: {
  beatId: number;
  colaboradores: ColaboradorBeatResponse[];
  acuerdo: AcuerdoCreditosResponse | null;
  onCambio: () => void;
}) {
  const [email, setEmail] = useState("");
  const [rol, setRol] = useState<RolColaborador>("CO_PRODUCTOR");
  const [porcentaje, setPorcentaje] = useState("25");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [trabajando, setTrabajando] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const suma = colaboradores.reduce((acc, c) => acc + c.porcentajePropuesto, 0);
  const sumaOk = colaboradores.length === 0 || Math.abs(suma - 100) < 0.001;

  async function invitar(evento: React.FormEvent) {
    evento.preventDefault();
    setTrabajando(true);
    setError(null);
    try {
      const usuario = await buscarUsuarioPorEmail(email.trim());
      const datos: ColaboradorBeatRequest = {
        beatId,
        usuarioId: usuario.id,
        rol,
        porcentajePropuesto: Number(porcentaje),
      };
      if (editandoId) {
        await actualizarColaborador(editandoId, datos);
      } else {
        await invitarColaborador(datos);
      }
      setEmail("");
      setPorcentaje("25");
      setEditandoId(null);
      onCambio();
    } catch (e) {
      setError(
        e instanceof ApiError
          ? e.message
          : "No se pudo invitar. Verifica que el email exista y el % sea válido.",
      );
    } finally {
      setTrabajando(false);
    }
  }

  function editar(col: ColaboradorBeatResponse) {
    setEditandoId(col.id);
    // El email no se puede deducir del id sin otra llamada; se deja el campo para re-escribirlo.
    setEmail("");
    setRol(col.rol);
    setPorcentaje(String(col.porcentajePropuesto));
  }

  async function quitar(id: number, nombre: string) {
    if (!confirm(`¿Quitar a ${nombre} del split? Esto reabre el acuerdo.`)) return;
    setError(null);
    try {
      await eliminarColaborador(id);
      onCambio();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "No se pudo quitar al colaborador.");
    }
  }

  async function abrir() {
    setError(null);
    try {
      await abrirAcuerdo({ beatId });
      onCambio();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "No se pudo abrir el acuerdo.");
    }
  }

  async function cerrar() {
    if (!acuerdo || !confirm("¿Eliminar el acuerdo abierto?")) return;
    setError(null);
    try {
      await eliminarAcuerdo(acuerdo.id);
      onCambio();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "No se pudo eliminar el acuerdo.");
    }
  }

  return (
    <section className="rounded-2xl border border-neutral-200 bg-white p-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="font-bold">Split de créditos</h2>
          <p className="text-sm text-neutral-500">
            La suma debe dar 100% y todos deben aceptar antes de publicar.
          </p>
        </div>
        <div className="flex items-center gap-2">
          {acuerdo ? (
            <span
              className={`rounded-full px-3 py-1 text-xs font-bold ${
                acuerdo.estado === "CERRADO"
                  ? "bg-green-100 text-green-800"
                  : "bg-yellow-100 text-yellow-800"
              }`}
            >
              Acuerdo {ETIQUETA_ESTADO_ACUERDO[acuerdo.estado]}
            </span>
          ) : (
            <button
              type="button"
              onClick={abrir}
              className="rounded-full bg-neutral-900 px-4 py-1.5 text-sm font-medium text-white hover:bg-brand-700"
            >
              Abrir acuerdo
            </button>
          )}
          {acuerdo?.estado === "ABIERTO" && (
            <button
              type="button"
              onClick={cerrar}
              className="rounded-full border border-neutral-300 px-3 py-1.5 text-xs text-neutral-500 hover:bg-neutral-100"
            >
              Eliminar acuerdo
            </button>
          )}
        </div>
      </div>

      <div className="mt-3 rounded-xl bg-neutral-50 p-3 text-sm">
        Suma actual:{" "}
        <span className={`font-bold ${sumaOk ? "text-green-700" : "text-red-600"}`}>
          {Number(suma.toFixed(2))}%
        </span>
        {!sumaOk && <span className="text-red-600"> — debe llegar a 100% para publicar.</span>}
        {acuerdo?.estado === "CERRADO" && (
          <span className="text-green-700"> — acuerdo cerrado, listo para publicar.</span>
        )}
      </div>

      {error && (
        <div className="mt-3">
          <FormAlert mensaje={error} />
        </div>
      )}

      <form onSubmit={invitar} className="mt-4 grid gap-3 rounded-xl bg-neutral-50 p-4 sm:grid-cols-4">
        <div className="sm:col-span-2">
          <label className="block text-xs font-medium text-neutral-600">
            Email del colaborador *
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder="colabora@ejemplo.com"
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-neutral-600">Rol *</label>
          <select
            value={rol}
            onChange={(e) => setRol(e.target.value as RolColaborador)}
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm"
          >
            {ROLES.map((r) => (
              <option key={r} value={r}>
                {ETIQUETA_ROL_COLABORADOR[r]}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-neutral-600">% *</label>
          <input
            type="number"
            min={0.01}
            max={100}
            step="0.01"
            value={porcentaje}
            onChange={(e) => setPorcentaje(e.target.value)}
            required
            className="mt-1 w-full rounded-xl border border-neutral-300 bg-white px-3 py-2 text-sm"
          />
        </div>
        <div className="flex gap-2 sm:col-span-4">
          <button
            type="submit"
            disabled={trabajando}
            className="rounded-full bg-brand-600 px-5 py-1.5 text-sm font-medium text-white hover:bg-brand-700 disabled:opacity-50"
          >
            {trabajando ? "Guardando..." : editandoId ? "Guardar cambios" : "Invitar"}
          </button>
          {editandoId && (
            <button
              type="button"
              onClick={() => {
                setEditandoId(null);
                setEmail("");
              }}
              className="rounded-full border border-neutral-300 px-5 py-1.5 text-sm text-neutral-600 hover:bg-neutral-100"
            >
              Cancelar edición
            </button>
          )}
        </div>
        {editandoId && (
          <p className="text-xs text-neutral-500 sm:col-span-4">
            Editando invitación: vuelve a escribir el email del colaborador (se necesita su id).
          </p>
        )}
      </form>

      <div className="mt-4 space-y-2">
        {colaboradores.length === 0 && (
          <p className="rounded-xl bg-neutral-100 p-3 text-sm text-neutral-500">
            Sin colaboradores: este beat se publica como trabajo 100% solista.
          </p>
        )}
        {colaboradores.map((col) => (
          <div
            key={col.id}
            className="flex flex-col gap-2 rounded-xl border border-neutral-200 p-3 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <p className="text-sm font-bold">
                {col.usuarioNombre} · {ETIQUETA_ROL_COLABORADOR[col.rol]} · {col.porcentajePropuesto}%
              </p>
              <span
                className={`mt-1 inline-block rounded-full px-2 py-0.5 text-xs font-semibold ${
                  col.estado === "ACEPTADO"
                    ? "bg-green-100 text-green-800"
                    : col.estado === "RECHAZADO"
                      ? "bg-red-100 text-red-700"
                      : "bg-yellow-100 text-yellow-800"
                }`}
              >
                {ETIQUETA_ESTADO_COLABORADOR[col.estado]}
              </span>
            </div>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => editar(col)}
                className="rounded-full border border-neutral-300 px-3 py-1 text-xs font-medium hover:bg-neutral-100"
              >
                Editar
              </button>
              <button
                type="button"
                onClick={() => quitar(col.id, col.usuarioNombre)}
                className="rounded-full border border-red-200 px-3 py-1 text-xs font-medium text-red-600 hover:bg-red-50"
              >
                Quitar
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
