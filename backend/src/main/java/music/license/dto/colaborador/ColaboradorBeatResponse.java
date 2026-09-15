package music.license.dto.colaborador;

import java.math.BigDecimal;

import music.license.model.ColaboradorBeat;

public class ColaboradorBeatResponse {

    private Long id;
    private Long beatId;
    private Long usuarioId;
    private String usuarioNombre;
    private String rol;
    private BigDecimal porcentajePropuesto;
    private String estado;

    public static ColaboradorBeatResponse desde(ColaboradorBeat colaboradorBeat) {
        ColaboradorBeatResponse dto = new ColaboradorBeatResponse();
        dto.id = colaboradorBeat.getId();
        dto.rol = colaboradorBeat.getRol() != null ? colaboradorBeat.getRol().name() : null;
        dto.porcentajePropuesto = colaboradorBeat.getPorcentajePropuesto();
        dto.estado = colaboradorBeat.getEstado() != null ? colaboradorBeat.getEstado().name() : null;

        if (colaboradorBeat.getBeat() != null) {
            dto.beatId = colaboradorBeat.getBeat().getId();
        }

        if (colaboradorBeat.getUsuario() != null) {
            dto.usuarioId = colaboradorBeat.getUsuario().getId();
            dto.usuarioNombre = colaboradorBeat.getUsuario().getNombre();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getBeatId() {
        return beatId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public String getRol() {
        return rol;
    }

    public BigDecimal getPorcentajePropuesto() {
        return porcentajePropuesto;
    }

    public String getEstado() {
        return estado;
    }
}
