package music.license.dto.colaborador;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import music.license.model.RolColaborador;

public class ColaboradorBeatRequest {

    @NotNull(message = "El beat es obligatorio")
    private Long beatId;

    @NotNull(message = "El usuario colaborador es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El rol del colaborador es obligatorio")
    private RolColaborador rol;

    @NotNull(message = "El porcentaje propuesto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El porcentaje debe ser mayor a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede superar 100")
    private BigDecimal porcentajePropuesto;

    public Long getBeatId() {
        return beatId;
    }

    public void setBeatId(Long beatId) {
        this.beatId = beatId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public RolColaborador getRol() {
        return rol;
    }

    public void setRol(RolColaborador rol) {
        this.rol = rol;
    }

    public BigDecimal getPorcentajePropuesto() {
        return porcentajePropuesto;
    }

    public void setPorcentajePropuesto(BigDecimal porcentajePropuesto) {
        this.porcentajePropuesto = porcentajePropuesto;
    }
}
