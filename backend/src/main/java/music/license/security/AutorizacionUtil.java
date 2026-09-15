package music.license.security;

import org.springframework.security.access.AccessDeniedException;

import music.license.model.Rol;
import music.license.model.Usuario;

public final class AutorizacionUtil {

    private AutorizacionUtil() {
    }

    public static void exigirRol(Usuario solicitante, Rol rolRequerido) {
        if (solicitante.getRol() != rolRequerido) {
            throw new AccessDeniedException(
                    "Esta accion requiere el rol " + rolRequerido.name());
        }
    }

    public static void exigirPropietario(Usuario propietarioRecurso, Usuario solicitante, String mensaje) {
        if (!propietarioRecurso.getId().equals(solicitante.getId())) {
            throw new AccessDeniedException(mensaje);
        }
    }
}
