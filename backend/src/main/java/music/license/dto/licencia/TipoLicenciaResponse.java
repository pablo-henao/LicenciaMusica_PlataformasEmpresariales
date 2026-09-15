package music.license.dto.licencia;

import java.math.BigDecimal;

import music.license.model.TipoLicencia;

public class TipoLicenciaResponse {

    private Long id;
    private Long beatId;
    private String beatTitulo;
    private String tipo;
    private BigDecimal precio;
    private String condiciones;

    public static TipoLicenciaResponse desde(TipoLicencia tipoLicencia) {
        TipoLicenciaResponse dto = new TipoLicenciaResponse();
        dto.id = tipoLicencia.getId();
        dto.tipo = tipoLicencia.getTipo() != null ? tipoLicencia.getTipo().name() : null;
        dto.precio = tipoLicencia.getPrecio();
        dto.condiciones = tipoLicencia.getCondiciones();

        if (tipoLicencia.getBeat() != null) {
            dto.beatId = tipoLicencia.getBeat().getId();
            dto.beatTitulo = tipoLicencia.getBeat().getTitulo();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getBeatId() {
        return beatId;
    }

    public String getBeatTitulo() {
        return beatTitulo;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getCondiciones() {
        return condiciones;
    }
}
