package music.license.dto.acuerdo;

import java.time.LocalDateTime;

import music.license.model.AcuerdoCreditosEvento;

public class AcuerdoCreditosEventoResponse {

    private Long id;
    private String tipo;
    private String detalle;
    private LocalDateTime fecha;
    private Long usuarioId;
    private String usuarioNombre;

    public static AcuerdoCreditosEventoResponse desde(AcuerdoCreditosEvento evento) {
        AcuerdoCreditosEventoResponse dto = new AcuerdoCreditosEventoResponse();
        dto.id = evento.getId();
        dto.tipo = evento.getTipo() != null ? evento.getTipo().name() : null;
        dto.detalle = evento.getDetalle();
        dto.fecha = evento.getFecha();

        if (evento.getUsuario() != null) {
            dto.usuarioId = evento.getUsuario().getId();
            dto.usuarioNombre = evento.getUsuario().getNombre();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }
}
