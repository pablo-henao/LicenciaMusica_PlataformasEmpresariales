import { Link } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import { EpicHero } from "../../components/info/EpicHero";

/**
 * Página pública /nosotros — solo diseño, sin BD ni llamadas /api.
 * Usa stats estáticas para no depender del backend.
 */
export function NosotrosPage() {
  const { usuario } = useAuth();
  const destinoVender = usuario?.rol === "PRODUCTOR" ? "/mis-beats" : "/registro";
  const mostrarVender = !usuario || usuario.rol !== "COMPRADOR";

  return (
    <div className="space-y-5">
      <EpicHero
        palabra="NOSOTROS"
        kicker="Nosotros — marketplace de beats"
        titulo="Beats con contrato y créditos"
        descripcion="Licencia+ es la vitrina donde productores publican beats, venden licencias con contrato en PDF y reparten créditos con colaboradores al 100%."
      >
        <Link
          to={usuario ? "/catalogo" : "/registro"}
          className="rounded-full bg-white px-6 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
        >
          Explorar beats
        </Link>
        {mostrarVender && (
          <Link
            to={destinoVender}
            className="rounded-full border border-white/25 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-white/10"
          >
            Vende tu primer beat
          </Link>
        )}
      </EpicHero>

      <section className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <dl className="flex gap-8 sm:gap-12">
          <div>
            <dd className="text-3xl font-bold tracking-tight sm:text-4xl">120+</dd>
            <dt className="mt-1 text-sm text-neutral-600">Beats publicados</dt>
          </div>
          <div>
            <dd className="text-3xl font-bold tracking-tight sm:text-4xl">40+</dd>
            <dt className="mt-1 text-sm text-neutral-600">Productores activos</dt>
          </div>
          <div>
            <dd className="text-3xl font-bold tracking-tight sm:text-4xl">300+</dd>
            <dt className="mt-1 text-sm text-neutral-600">Contratos generados</dt>
          </div>
        </dl>
        <p className="mt-4 max-w-3xl text-sm leading-relaxed text-neutral-600">
          Creemos en la música sin letra pequeña: licencias claras por beat, splits acordados antes
          de vender y un PDF que respalda cada compra para ambas partes. Trap, reggaetón, dembow y
          bachata, con preview para escuchar antes de comprar.
        </p>
      </section>

      <section className="rounded-2xl bg-neutral-900 p-6 text-white sm:p-8">
        <p className="text-[11px] font-medium tracking-[0.2em] text-white/50 uppercase">
          Cómo funciona
        </p>
        <div className="mt-4 grid gap-5 md:grid-cols-3">
          <div>
            <p className="font-display text-3xl font-black text-white/90">01</p>
            <h3 className="mt-1 font-bold">Publica</h3>
            <p className="mt-1 text-sm text-white/60">
              Sube tu beat con su preview, define género y BPM, y declara a tus colaboradores.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-white/90">02</p>
            <h3 className="mt-1 font-bold">Acuerda</h3>
            <p className="mt-1 text-sm text-white/60">
              El split debe sumar 100% y cada colaborador acepta su parte antes de vender.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-white/90">03</p>
            <h3 className="mt-1 font-bold">Vende con respaldo</h3>
            <p className="mt-1 text-sm text-white/60">
              Cada compra emite su contrato en PDF con los términos aceptados por ambas partes.
            </p>
          </div>
        </div>
        <div className="mt-6 flex flex-wrap gap-3">
          {mostrarVender && (
            <Link
              to={destinoVender}
              className="rounded-full bg-white px-5 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
            >
              Vende tu primer beat
            </Link>
          )}
          <Link
            to="/licencias"
            className="rounded-full border border-white/25 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-white/10"
          >
            Ver licencias
          </Link>
          <Link
            to="/splits"
            className="rounded-full border border-white/25 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-white/10"
          >
            Ver splits
          </Link>
        </div>
      </section>
    </div>
  );
}
