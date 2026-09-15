package music.license.dto.licencia;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import music.license.model.TipoLicenciaEnum;

public class TipoLicenciaRequest {

    @NotNull(message = "El beat es obligatorio")
    private Long beatId;

    @NotNull(message = "El tipo de licencia es obligatorio")
    private TipoLicenciaEnum tipo;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    private String condiciones;

    public Long getBeatId() {
        return beatId;
    }

    public void setBeatId(Long beatId) {
        this.beatId = beatId;
    }

    public TipoLicenciaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoLicenciaEnum tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }
}
