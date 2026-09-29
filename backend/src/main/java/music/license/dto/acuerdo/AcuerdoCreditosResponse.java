package music.license.dto.acuerdo;

import java.time.LocalDateTime;

import music.license.model.AcuerdoCreditos;

public class AcuerdoCreditosResponse {

    private Long id;
    private Long beatId;
    private String beatTitulo;
    private String estado;
    private LocalDateTime fechaCierre;

    public static AcuerdoCreditosResponse desde(AcuerdoCreditos acuerdoCreditos) {
        AcuerdoCreditosResponse dto = new AcuerdoCreditosResponse();
        dto.id = acuerdoCreditos.getId();
        dto.estado = acuerdoCreditos.getEstado() != null ? acuerdoCreditos.getEstado().name() : null;
        dto.fechaCierre = acuerdoCreditos.getFechaCierre();

        if (acuerdoCreditos.getBeat() != null) {
            dto.beatId = acuerdoCreditos.getBeat().getId();
            dto.beatTitulo = acuerdoCreditos.getBeat().getTitulo();
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

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }
}
