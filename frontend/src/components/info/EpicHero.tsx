import type { ReactNode } from "react";

interface EpicHeroProps {
  palabra: string;
  kicker: string;
  titulo: string;
  descripcion: string;
  children?: ReactNode;
}

/**
 * Hero cinematográfico oscuro compartido (100% CSS, sin imágenes externas).
 * Reutiliza las clases .hero-winzy / .hero-grain / .hero-word de index.css.
 */
export function EpicHero({ palabra, kicker, titulo, descripcion, children }: EpicHeroProps) {
  return (
    <section className="hero-winzy relative -mt-6 ml-[calc(50%-50vw)] w-[100vw] overflow-hidden text-white">
      <div className="hero-grain pointer-events-none absolute inset-0" />
      <p aria-hidden="true" className="hero-word pointer-events-none mt-6 text-center">
        {palabra}
      </p>
      <div className="relative mx-auto w-full max-w-6xl px-4 pb-10 sm:px-6">
        <p className="text-[11px] font-medium tracking-[0.2em] text-white/50 uppercase">{kicker}</p>
        <h1 className="font-display mt-2 max-w-2xl text-3xl font-black tracking-tight uppercase sm:text-5xl">
          {titulo}
        </h1>
        <p className="mt-3 max-w-xl text-sm leading-relaxed text-white/70 sm:text-base">{descripcion}</p>
        {children && <div className="mt-6 flex flex-wrap items-center gap-3">{children}</div>}
      </div>
    </section>
  );
}
