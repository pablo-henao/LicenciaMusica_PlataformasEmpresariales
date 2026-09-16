package music.license.dto.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * Envoltorio simple para respuestas paginadas: evita serializar org.springframework.data.domain.Page
 * directamente (formato interno inestable entre versiones de Spring Data) y mantiene la forma de la
 * respuesta igual para cualquier listado paginado de la API.
 */
public class PaginaResponse<T> {

    private final List<T> contenido;
    private final int pagina;
    private final int tamano;
    private final long totalElementos;
    private final int totalPaginas;

    public PaginaResponse(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
        this.contenido = contenido;
        this.pagina = pagina;
        this.tamano = tamano;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
    }

    public static <E, T> PaginaResponse<T> desde(Page<E> pagina, Function<E, T> mapeo) {
        List<T> contenido = pagina.getContent().stream().map(mapeo).toList();

        return new PaginaResponse<>(
                contenido,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
    }

    public List<T> getContenido() {
        return contenido;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamano() {
        return tamano;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}
