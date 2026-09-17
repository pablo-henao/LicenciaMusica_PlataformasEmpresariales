package music.license.security;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;

import music.license.model.ColaboradorBeat;
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

    /**
     * Verifica (sin lanzar nada) si el solicitante es el productor dueño del beat o
     * uno de los colaboradores declarados en el. Usado para decidir visibilidad de
     * datos de un beat que no son publicos (licencias de un borrador, split de
     * creditos, historial de creditos) - cada llamador decide que excepcion lanzar
     * si esto da false (404 para "ocultar", 403 para "prohibir").
     */
    public static boolean esDuenioOColaborador(
            Usuario productorDelBeat, Usuario solicitante, List<ColaboradorBeat> colaboradoresDelBeat) {

        boolean esDuenio = productorDelBeat.getId().equals(solicitante.getId());
        boolean esColaborador = colaboradoresDelBeat.stream()
                .anyMatch(c -> c.getUsuario().getId().equals(solicitante.getId()));

        return esDuenio || esColaborador;
    }
}
