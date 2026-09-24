import { Link } from "react-router-dom";
import { obtenerCatalogo } from "../api/beats";
import { useAuth } from "../context/useAuth";
import { useApiFetch } from "../hooks/useApiFetch";

/**
 * Landing estilo Winzy: hero cinematográfico oscuro con palabra gigante,
 * stats, CTA + card flotante. El resto de la app conserva su tema claro.
 */
export function HomePage() {
  const { usuario } = useAuth();
  const destinoTienda = usuario ? "/catalogo" : "/registro";
  const destinoVender = usuario?.rol === "PRODUCTOR" ? "/mis-beats" : "/registro";

  return (
    <div className="space-y-5">
      <section className="hero-winzy relative -mt-6 ml-[calc(50%-50vw)] w-[100vw] overflow-hidden text-white">
        <div className="hero-grain pointer-events-none absolute inset-0" />

        {/* Palabra gigante de fondo, a todo el ancho */}
        <p aria-hidden="true" className="hero-word pointer-events-none mt-6 text-center">
          LICENCIA+
        </p>

        <div className="relative mx-auto w-full max-w-6xl px-4 pb-8 sm:px-6">
          <div className="mt-10 flex flex-col gap-8 lg:flex-row lg:items-end lg:justify-between">
            <div className="max-w-xl">
              {usuario ? <StatsReales /> : <StatsLanzamiento />}

              <div className="mt-6 flex flex-wrap items-center gap-3">
                <Link
                  to={destinoTienda}
                  className="rounded-full bg-white px-6 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
                >
                  Explorar beats
                </Link>
                <Link
                  to={destinoTienda}
                  aria-label="Explorar beats"
                  className="flex h-10 w-10 items-center justify-center rounded-full bg-white text-lg text-neutral-900 transition hover:bg-neutral-200"
                >
                  ↗
                </Link>
                <p className="w-full text-sm leading-relaxed text-white/70 sm:max-w-md sm:w-auto sm:ml-2">
                  Beats de trap, reggaetón, dembow y bachata con licencia clara,
                  contrato en PDF y créditos repartidos con tus colaboradores.
                </p>
              </div>
            </div>

            {usuario ? <BeatDestacado /> : <CardDemo />}
          </div>
        </div>
      </section>

      {/* Pilares */}
      <section id="licencias" className="grid scroll-mt-24 gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-neutral-200 bg-white p-5">
          <p className="text-2xl">◉</p>
          <h2 className="mt-2 font-bold">Licencias claras</h2>
          <p className="mt-1 text-sm text-neutral-600">
            Exclusiva, no exclusiva o comercial limitada — con precio y condiciones por beat.
          </p>
        </div>
        <div id="splits" className="scroll-mt-24 rounded-2xl border border-neutral-200 bg-white p-5">
          <p className="text-2xl">◈</p>
          <h2 className="mt-2 font-bold">Splits al 100%</h2>
          <p className="mt-1 text-sm text-neutral-600">
            Declara productor, co-productor, vocalista, mezcla y mastering. Todos aceptan antes de
            vender.
          </p>
        </div>
        <div className="rounded-2xl border border-neutral-200 bg-white p-5">
          <p className="text-2xl">⬣</p>
          <h2 className="mt-2 font-bold">Contrato en PDF</h2>
          <p className="mt-1 text-sm text-neutral-600">
            Cada checkout genera un contrato congelado con las partes, la obra y los términos.
          </p>
        </div>
      </section>

      {/* Nosotros / cómo funciona */}
      <section id="nosotros" className="scroll-mt-24 rounded-2xl bg-neutral-900 p-6 text-white sm:p-8">
        <p className="text-[11px] font-medium tracking-[0.2em] text-white/50 uppercase">
          Nosotros — cómo funciona
        </p>
        <div className="mt-4 grid gap-5 md:grid-cols-3">
          <div>
            <p className="font-display text-3xl font-black text-white/90">01</p>
            <h3 className="mt-1 font-bold">Publica</h3>
            <p className="mt-1 text-sm text-white/60">
              Sube tu beat con su preview, define géneros y BPM, y declara a tus colaboradores.
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
          <Link
            to={destinoVender}
            className="rounded-full bg-white px-5 py-2.5 text-sm font-semibold text-neutral-900 transition hover:bg-neutral-200"
          >
            Vende tu primer beat
          </Link>
          {usuario && (
            <Link
              to="/mis-invitaciones"
              className="rounded-full border border-white/25 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-white/10"
            >
              Ver mis splits
            </Link>
          )}
        </div>
      </section>
    </div>
  );
}

function StatsLanzamiento() {
  const stats = [
    { valor: "120+", etiqueta: "Beats publicados" },
    { valor: "40+", etiqueta: "Productores activos" },
    { valor: "300+", etiqueta: "Contratos generados" },
  ];
  return (
    <dl className="flex gap-8 sm:gap-12">
      {stats.map((s) => (
        <div key={s.etiqueta}>
          <dd className="text-3xl font-bold tracking-tight sm:text-4xl">{s.valor}</dd>
          <dt className="mt-1 text-sm text-white/70">{s.etiqueta}</dt>
        </div>
      ))}
    </dl>
  );
}

function StatsReales() {
  const { datos } = useApiFetch(() => obtenerCatalogo({ page: 0, size: 1 }), [], "stats");
  const beats = datos ? `${datos.totalElementos}+` : "···";
  const stats = [
    { valor: beats, etiqueta: "Beats publicados" },
    { valor: "40+", etiqueta: "Productores activos" },
    { valor: "300+", etiqueta: "Contratos generados" },
  ];
  return (
    <dl className="flex gap-8 sm:gap-12">
      {stats.map((s) => (
        <div key={s.etiqueta}>
          <dd className="text-3xl font-bold tracking-tight sm:text-4xl">{s.valor}</dd>
          <dt className="mt-1 text-sm text-white/70">{s.etiqueta}</dt>
        </div>
      ))}
    </dl>
  );
}

function CardDemo() {
  return (
    <div className="w-full max-w-xs rounded-2xl border border-white/15 bg-white/10 p-3 backdrop-blur-md lg:w-64">
      <div className="flex h-32 items-center justify-center rounded-xl bg-gradient-to-br from-amber-500/80 to-brand-800 text-4xl">
        ♪
      </div>
      <div className="mt-2 flex items-center justify-between text-sm">
        <span className="text-white/60">01/10</span>
        <span className="h-1 w-16 overflow-hidden rounded-full bg-white/20">
          <span className="block h-full w-1/4 rounded-full bg-white" />
        </span>
      </div>
      <p className="mt-1 text-sm font-semibold">Dembow · 96 BPM</p>
      <p className="text-xs text-white/60">El beat destacado de la semana</p>
    </div>
  );
}

function BeatDestacado() {
  const { datos } = useApiFetch(() => obtenerCatalogo({ page: 0, size: 4 }), [], "destacado");
  const beats = datos?.contenido ?? [];
  const total = datos?.totalElementos ?? 0;
  const primero = beats[0];

  if (!primero) {
    return <CardDemo />;
  }

  return (
    <Link
      to={`/beats/${primero.id}`}
      className="block w-full max-w-xs rounded-2xl border border-white/15 bg-white/10 p-3 backdrop-blur-md transition hover:bg-white/15 lg:w-64"
    >
      <div className="flex h-32 items-center justify-center rounded-xl bg-gradient-to-br from-amber-500/80 to-brand-800 text-4xl">
        ♪
      </div>
      <div className="mt-2 flex items-center justify-between text-sm">
        <span className="text-white/60">01/{String(Math.min(Math.max(total, 1), 99)).padStart(2, "0")}</span>
        <span className="h-1 w-16 overflow-hidden rounded-full bg-white/20">
          <span
            className="block h-full rounded-full bg-white"
            style={{ width: `${Math.max(8, Math.min(100, (1 / Math.max(beats.length, 1)) * 100))}%` }}
          />
        </span>
      </div>
      <p className="mt-1 truncate text-sm font-semibold">{primero.titulo}</p>
      <p className="truncate text-xs text-white/60">
        {primero.genero} · {primero.bpm} BPM · Por {primero.productorNombre}
      </p>
    </Link>
  );
}
