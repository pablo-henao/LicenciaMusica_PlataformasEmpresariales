package music.license.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String mensaje;
    private final List<String> detalles;

    public ApiError(int status, String error, String mensaje, List<String> detalles) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<String> getDetalles() {
        return detalles;
    }
}
