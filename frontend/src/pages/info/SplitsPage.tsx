import { Link } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import { EpicHero } from "../../components/info/EpicHero";

/**
 * Página pública /splits — solo diseño, sin BD ni llamadas /api.
 */
export function SplitsPage() {
  const { usuario } = useAuth();
  const esProductor = usuario?.rol === "PRODUCTOR" || usuario?.rol === "ADMIN";
  const destinoSplits = usuario ? (esProductor ? "/mis-invitaciones" : "/catalogo") : "/registro";

  return (
    <div className="space-y-5">
      <EpicHero
        palabra="SPLITS"
        kicker="Splits — créditos al 100%"
        titulo="Reparte créditos sin peleas"
        descripcion="Declara a tus colaboradores con su porcentaje. El split debe sumar 100% y todos aceptan su parte antes de que el beat pueda venderse."
      >
        {esProductor ? (
          <Link
            to="/mis-invitaciones"
            className="rounded-full bg-white px-6 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
          >
            Ver mis splits
          </Link>
        ) : (
          <Link
            to={destinoSplits}
            className="rounded-full bg-white px-6 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
          >
            Explorar beats
          </Link>
        )}
        <Link
          to="/licencias"
          className="rounded-full border border-white/25 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-white/10"
        >
          Ver licencias
        </Link>
      </EpicHero>

      <section className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h2 className="text-lg font-bold">Cómo funciona un split</h2>
        <div className="mt-4 grid gap-5 md:grid-cols-4">
          <div>
            <p className="font-display text-3xl font-black text-neutral-900/80">01</p>
            <h3 className="mt-1 font-bold">Declara</h3>
            <p className="mt-1 text-sm text-neutral-600">
              Invita por email a cada colaborador con su rol y porcentaje propuesto.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-neutral-900/80">02</p>
            <h3 className="mt-1 font-bold">Suma 100%</h3>
            <p className="mt-1 text-sm text-neutral-600">
              El acuerdo solo es válido cuando los porcentajes suman exactamente 100%.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-neutral-900/80">03</p>
            <h3 className="mt-1 font-bold">Todos aceptan</h3>
            <p className="mt-1 text-sm text-neutral-600">
              Cada invitado acepta o rechaza desde Mis splits. Sin pendientes, no hay venta.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-neutral-900/80">04</p>
            <h3 className="mt-1 font-bold">Publica y vende</h3>
            <p className="mt-1 text-sm text-neutral-600">
              Con el acuerdo cerrado, publica el beat y cada compra queda respaldada.
            </p>
          </div>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-2">
        <div className="rounded-2xl border border-neutral-200 bg-white p-5">
          <h2 className="font-bold">Roles de colaboración</h2>
          <ul className="mt-3 space-y-2 text-sm text-neutral-600">
            <li>• <span className="font-medium text-neutral-900">Productor</span> — dueño del beat</li>
            <li>• <span className="font-medium text-neutral-900">Co-productor</span> — beatmaking compartido</li>
            <li>• <span className="font-medium text-neutral-900">Vocalista</span> — hooks, versos, coros</li>
            <li>• <span className="font-medium text-neutral-900">Mezcla / Mastering</span> — sonido final</li>
          </ul>
        </div>
        <div className="rounded-2xl bg-neutral-900 p-6 text-white sm:p-8">
          <p className="text-[11px] font-medium tracking-[0.2em] text-white/50 uppercase">
            Regla de oro
          </p>
          <p className="font-display mt-2 text-2xl font-black uppercase">
            100% + todos aceptados = venta
          </p>
          <p className="mt-2 text-sm text-white/60">
            Si falta un porcentaje o alguien no aceptó, el beat se queda en borrador. Así nadie
            vende lo que no tiene acordado.
          </p>
        </div>
      </section>
    </div>
  );
}
