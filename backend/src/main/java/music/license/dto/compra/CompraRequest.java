package music.license.dto.compra;

import jakarta.validation.constraints.NotNull;

public class CompraRequest {

    @NotNull(message = "El tipo de licencia es obligatorio")
    private Long tipoLicenciaId;

    public Long getTipoLicenciaId() {
        return tipoLicenciaId;
    }

    public void setTipoLicenciaId(Long tipoLicenciaId) {
        this.tipoLicenciaId = tipoLicenciaId;
    }
}
