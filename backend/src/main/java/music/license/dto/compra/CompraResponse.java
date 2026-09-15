package music.license.dto.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import music.license.model.Compra;

public class CompraResponse {

    private Long id;
    private Long compradorId;
    private String compradorNombre;
    private Long tipoLicenciaId;
    private String beatTitulo;
    private BigDecimal precio;
    private LocalDateTime fecha;
    private String estado;
    private boolean contratoDisponible;

    public static CompraResponse desde(Compra compra) {
        CompraResponse dto = new CompraResponse();
        dto.id = compra.getId();
        dto.fecha = compra.getFecha();
        dto.estado = compra.getEstado() != null ? compra.getEstado().name() : null;
        dto.contratoDisponible = compra.getContratoPdf() != null;

        if (compra.getComprador() != null) {
            dto.compradorId = compra.getComprador().getId();
            dto.compradorNombre = compra.getComprador().getNombre();
        }

        if (compra.getTipoLicencia() != null) {
            dto.tipoLicenciaId = compra.getTipoLicencia().getId();
            dto.precio = compra.getTipoLicencia().getPrecio();

            if (compra.getTipoLicencia().getBeat() != null) {
                dto.beatTitulo = compra.getTipoLicencia().getBeat().getTitulo();
            }
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getCompradorId() {
        return compradorId;
    }

    public String getCompradorNombre() {
        return compradorNombre;
    }

    public Long getTipoLicenciaId() {
        return tipoLicenciaId;
    }

    public String getBeatTitulo() {
        return beatTitulo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isContratoDisponible() {
        return contratoDisponible;
    }
}
