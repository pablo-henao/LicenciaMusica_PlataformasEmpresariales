import { useEffect, useState } from "react";

interface EstadoFetch<T> {
  datos: T | null;
  cargando: boolean;
  error: string | null;
}

/**
 * Encapsula el patron "cargar datos al montar / cuando cambian ciertas dependencias"
 * que se repite en casi toda pantalla de esta app. Encadena todo con promesas (en vez
 * de setState suelto al inicio del efecto) para cumplir con la regla de ESLint
 * react-hooks/set-state-in-effect, que prohibe llamar a setState de forma sincronica
 * en el cuerpo de un efecto.
 *
 * `deps` controla cuando se vuelve a pedir los datos (igual que el array de useEffect);
 * a proposito NO incluye `fetcher` ahi, para no obligar a memoizarlo con useCallback
 * en cada pantalla que use este hook.
 */
export function useApiFetch<T>(
  fetcher: () => Promise<T>,
  deps: unknown[],
  mensajeError = "No se pudo cargar la información. Intenta de nuevo.",
): EstadoFetch<T> {
  const [datos, setDatos] = useState<T | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelado = false;

    Promise.resolve()
      .then(() => {
        setCargando(true);
        setError(null);
      })
      .then(fetcher)
      .then((resultado) => {
        if (!cancelado) {
          setDatos(resultado);
        }
      })
      .catch(() => {
        if (!cancelado) {
          setError(mensajeError);
        }
      })
      .finally(() => {
        if (!cancelado) {
          setCargando(false);
        }
      });

    return () => {
      cancelado = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps -- deps las controla el llamador a proposito
  }, deps);

  return { datos, cargando, error };
}
