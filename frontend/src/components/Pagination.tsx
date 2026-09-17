interface PaginationProps {
  pagina: number; // 0-indexado, igual que el backend
  totalPaginas: number;
  onCambiar: (pagina: number) => void;
}

export function Pagination({ pagina, totalPaginas, onCambiar }: PaginationProps) {
  if (totalPaginas <= 1) {
    return null;
  }

  return (
    <div className="mt-6 flex items-center justify-center gap-3">
      <button
        type="button"
        disabled={pagina === 0}
        onClick={() => onCambiar(pagina - 1)}
        className="rounded-md border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100 disabled:cursor-not-allowed disabled:opacity-40"
      >
        Anterior
      </button>
      <span className="text-sm text-neutral-600">
        Página {pagina + 1} de {totalPaginas}
      </span>
      <button
        type="button"
        disabled={pagina >= totalPaginas - 1}
        onClick={() => onCambiar(pagina + 1)}
        className="rounded-md border border-neutral-300 px-3 py-1.5 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100 disabled:cursor-not-allowed disabled:opacity-40"
      >
        Siguiente
      </button>
    </div>
  );
}
