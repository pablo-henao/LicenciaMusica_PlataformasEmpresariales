package music.license.dto.acuerdo;

import jakarta.validation.constraints.NotNull;

public class AcuerdoCreditosRequest {

    @NotNull(message = "El beat es obligatorio")
    private Long beatId;

    public Long getBeatId() {
        return beatId;
    }

    public void setBeatId(Long beatId) {
        this.beatId = beatId;
    }
}
