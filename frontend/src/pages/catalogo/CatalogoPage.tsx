import { useCallback, useState } from "react";
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
  const [filtros, setFiltros] = useState<Omit<FiltrosCatalogo, "page" | "size">>({});
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
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-semibold text-neutral-900">Catálogo de beats</h1>
        <p className="mt-1 text-sm text-neutral-600">Explora los beats publicados y escucha su preview.</p>
      </div>

      <FiltrosCatalogoForm onBuscar={alBuscar} />

      <div className="mt-6">
        {error && <FormAlert mensaje={error} />}

        {cargando && <p className="py-10 text-center text-neutral-500">Cargando beats...</p>}

        {!cargando && !error && resultado.contenido.length === 0 && (
          <p className="py-10 text-center text-neutral-500">
            No se encontraron beats con esos filtros.
          </p>
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
