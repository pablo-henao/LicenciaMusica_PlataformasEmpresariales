import { Link } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import { EpicHero } from "../../components/info/EpicHero";

/**
 * Página pública /licencias — solo diseño, sin BD ni llamadas /api.
 */
export function LicenciasPage() {
  const { usuario } = useAuth();
  const destinoTienda = usuario ? "/catalogo" : "/registro";

  return (
    <div className="space-y-5">
      <EpicHero
        palabra="LICENCIAS"
        kicker="Licencias — contrato claro"
        titulo="Compra y vende con reglas claras"
        descripcion="Cada beat define sus licencias con precio y condiciones visibles. Al comprar, se genera un contrato en PDF con las partes, la obra y los términos aceptados."
      >
        <Link
          to={destinoTienda}
          className="rounded-full bg-white px-6 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
        >
          Explorar beats
        </Link>
        <Link
          to="/nosotros"
          className="rounded-full border border-white/25 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-white/10"
        >
          Cómo funciona
        </Link>
      </EpicHero>

      <section className="grid gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-neutral-200 bg-white p-5">
          <p className="text-2xl">◉</p>
          <h2 className="mt-2 font-bold">No exclusiva</h2>
          <p className="mt-1 text-sm text-neutral-600">
            La opción accesible para maquetas y lanzamientos independientes. El productor puede
            seguir vendiéndola a otros artistas.
          </p>
          <ul className="mt-3 space-y-1 text-sm text-neutral-600">
            <li>• Precio visible por beat</li>
            <li>• Condiciones de uso publicadas</li>
            <li>• Contrato PDF por compra</li>
          </ul>
        </div>
        <div className="rounded-2xl border border-neutral-200 bg-white p-5">
          <p className="text-2xl">◈</p>
          <h2 className="mt-2 font-bold">Comercial limitada</h2>
          <p className="mt-1 text-sm text-neutral-600">
            Para artistas en crecimiento: más reproducciones, shows y monetización, con límites
            claros escritos en el contrato.
          </p>
          <ul className="mt-3 space-y-1 text-sm text-neutral-600">
            <li>• Alcance y usos delimitados</li>
            <li>• Vigencia y territorio claros</li>
            <li>• Contrato PDF por compra</li>
          </ul>
        </div>
        <div className="rounded-2xl border border-neutral-200 bg-white p-5">
          <p className="text-2xl">⬣</p>
          <h2 className="mt-2 font-bold">Exclusiva</h2>
          <p className="mt-1 text-sm text-neutral-600">
            El beat deja de venderse. Máximo control para tu lanzamiento comercial, con respaldo
            total en PDF.
          </p>
          <ul className="mt-3 space-y-1 text-sm text-neutral-600">
            <li>• Retiro del catálogo al vender</li>
            <li>• Uso comercial completo</li>
            <li>• Contrato PDF por compra</li>
          </ul>
        </div>
      </section>

      <section className="rounded-2xl bg-neutral-900 p-6 text-white sm:p-8">
        <p className="text-[11px] font-medium tracking-[0.2em] text-white/50 uppercase">
          Contrato en PDF
        </p>
        <h2 className="font-display mt-2 text-2xl font-black uppercase">Respaldo en cada checkout</h2>
        <div className="mt-4 grid gap-5 md:grid-cols-3">
          <div>
            <p className="font-display text-3xl font-black text-white/90">01</p>
            <h3 className="mt-1 font-bold">Las partes</h3>
            <p className="mt-1 text-sm text-white/60">
              Comprador y productor identificados, con fecha y licencia elegida.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-white/90">02</p>
            <h3 className="mt-1 font-bold">La obra</h3>
            <p className="mt-1 text-sm text-white/60">
              Título, género, BPM y condiciones de la licencia congelados al momento de la compra.
            </p>
          </div>
          <div>
            <p className="font-display text-3xl font-black text-white/90">03</p>
            <h3 className="mt-1 font-bold">Los términos</h3>
            <p className="mt-1 text-sm text-white/60">
              Usos permitidos, límites y vigencia, listos para descargar desde Mis compras.
            </p>
          </div>
        </div>
        <div className="mt-6">
          <Link
            to={destinoTienda}
            className="rounded-full bg-white px-5 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
          >
            Ver beats con licencia
          </Link>
        </div>
      </section>
    </div>
  );
}
