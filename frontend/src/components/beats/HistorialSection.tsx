import type { AcuerdoCreditosEventoResponse } from "../../api/types";
import { formatearFecha } from "../../utils/format";

const COLOR_TIPO: Record<string, string> = {
  ABIERTO: "bg-neutral-900 text-white",
  PROPUESTA: "bg-brand-100 text-brand-800",
  MODIFICACION: "bg-yellow-100 text-yellow-800",
  ACEPTACION: "bg-green-100 text-green-800",
  RECHAZO: "bg-red-100 text-red-700",
  ELIMINACION: "bg-red-100 text-red-700",
  CIERRE: "bg-green-600 text-white",
  REAPERTURA: "bg-orange-100 text-orange-800",
};

export function HistorialSection({ eventos }: { eventos: AcuerdoCreditosEventoResponse[] }) {
  return (
    <section className="rounded-2xl border border-neutral-200 bg-white p-5">
      <h2 className="font-bold">Historial del acuerdo</h2>
      <p className="text-sm text-neutral-500">Quién propuso, aceptó, rechazó o modificó.</p>
      {eventos.length === 0 ? (
        <p className="mt-3 rounded-xl bg-neutral-100 p-3 text-sm text-neutral-500">
          Sin eventos todavía.
        </p>
      ) : (
        <ol className="mt-4 space-y-3">
          {eventos.map((ev) => (
            <li key={ev.id} className="flex gap-3">
              <span className="mt-1.5 h-2.5 w-2.5 shrink-0 rounded-full bg-brand-500" />
              <div className="min-w-0">
                <p className="flex flex-wrap items-center gap-2 text-sm">
                  <span
                    className={`rounded-full px-2 py-0.5 text-xs font-bold ${COLOR_TIPO[ev.tipo] ?? "bg-neutral-100 text-neutral-700"}`}
                  >
                    {ev.tipo}
                  </span>
                  <span className="font-medium">{ev.usuarioNombre ?? "Sistema"}</span>
                  <span className="text-neutral-400">{formatearFecha(ev.fecha)}</span>
                </p>
                <p className="mt-0.5 text-sm text-neutral-600">{ev.detalle}</p>
              </div>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}
