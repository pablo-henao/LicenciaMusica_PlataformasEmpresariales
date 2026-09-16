package music.license.dto.notificacion;

import java.time.LocalDateTime;

import music.license.model.Notificacion;

public class NotificacionResponse {

    private Long id;
    private String tipo;
    private String mensaje;
    private boolean leida;
    private LocalDateTime fecha;

    public static NotificacionResponse desde(Notificacion notificacion) {
        NotificacionResponse dto = new NotificacionResponse();
        dto.id = notificacion.getId();
        dto.tipo = notificacion.getTipo() != null ? notificacion.getTipo().name() : null;
        dto.mensaje = notificacion.getMensaje();
        dto.leida = notificacion.isLeida();
        dto.fecha = notificacion.getFecha();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
