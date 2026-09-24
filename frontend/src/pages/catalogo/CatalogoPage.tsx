import { useCallback, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { obtenerCatalogo, type FiltrosCatalogo } from "../../api/beats";
import type { BeatResponse, PaginaResponse } from "../../api/types";
import { BeatCard } from "../../components/beats/BeatCard";
import { FiltrosCatalogoForm } from "../../components/beats/FiltrosCatalogoForm";
import { Pagination } from "../../components/Pagination";
import { FormAlert } from "../../components/FormAlert";
import { useApiFetch } from "../../hooks/useApiFetch";

const TAMANO_PAGINA = 12;
const PAGINA_VACIA: PaginaResponse<BeatResponse> = {
  contenido: [],
  pagina: 0,
  tamano: TAMANO_PAGINA,
  totalElementos: 0,
  totalPaginas: 0,
};

export function CatalogoPage() {
  const [params, setParams] = useSearchParams();
  const tituloInicial = params.get("titulo") ?? "";

  const [filtros, setFiltros] = useState<Omit<FiltrosCatalogo, "page" | "size">>(() =>
    tituloInicial ? { titulo: tituloInicial } : {},
  );
  const [pagina, setPagina] = useState(0);

  const fetcher = useCallback(
    () => obtenerCatalogo({ ...filtros, page: pagina, size: TAMANO_PAGINA }),
    [filtros, pagina],
  );

  const { datos, cargando, error } = useApiFetch(fetcher, [filtros, pagina], "No se pudo cargar el catálogo.");
  const resultado = datos ?? PAGINA_VACIA;

  function alBuscar(nuevosFiltros: Omit<FiltrosCatalogo, "page" | "size">) {
    setPagina(0);
    setFiltros(nuevosFiltros);
    if (nuevosFiltros.titulo) {
      setParams({ titulo: nuevosFiltros.titulo });
    } else {
      setParams({});
    }
  }

  return (
    <div>
      <div className="rounded-2xl border border-neutral-200 bg-white p-5 sm:p-6">
        <h1 className="text-2xl font-bold tracking-tight">Tienda de beats</h1>
        <p className="mt-1 text-sm text-neutral-600">
          Explora los beats publicados, escucha el preview y compra tu licencia con contrato.
        </p>
        <div className="mt-4">
          <FiltrosCatalogoForm onBuscar={alBuscar} />
        </div>
      </div>

      <div className="mt-5">
        {error && <FormAlert mensaje={error} />}

        {cargando && <p className="py-10 text-center text-neutral-500">Cargando beats...</p>}

        {!cargando && !error && resultado.contenido.length === 0 && (
          <div className="rounded-2xl border border-dashed border-neutral-300 bg-white py-12 text-center text-neutral-500">
            No se encontraron beats con esos filtros.
          </div>
        )}

        {!cargando && resultado.contenido.length > 0 && (
          <>
            <p className="mb-3 text-sm text-neutral-500">{resultado.totalElementos} beat(s) encontrados</p>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {resultado.contenido.map((beat) => (
                <BeatCard key={beat.id} beat={beat} />
              ))}
            </div>
          </>
        )}

        <Pagination pagina={pagina} totalPaginas={resultado.totalPaginas} onCambiar={setPagina} />
      </div>
    </div>
  );
}
