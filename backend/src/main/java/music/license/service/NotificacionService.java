package music.license.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import music.license.exception.ResourceNotFoundException;
import music.license.model.Notificacion;
import music.license.model.TipoNotificacion;
import music.license.model.Usuario;
import music.license.repository.NotificacionRepository;
import music.license.security.AutorizacionUtil;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    /**
     * Crea una notificacion in-app para el destinatario. Se llama desde otros
     * servicios (colaboradores, compras) en el punto donde ocurre el evento real,
     * nunca directamente desde un controlador.
     */
    public void crear(Usuario destinatario, TipoNotificacion tipo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(destinatario);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());

        notificacionRepository.save(notificacion);
    }

    public Page<Notificacion> obtenerMias(Usuario solicitante, Pageable pageable) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(solicitante.getId(), pageable);
    }

    public long contarNoLeidas(Usuario solicitante) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(solicitante.getId());
    }

    public Notificacion marcarLeida(Long id, Usuario solicitante) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));

        AutorizacionUtil.exigirPropietario(
                notificacion.getUsuario(), solicitante, "Solo el destinatario puede marcar esta notificación como leída");

        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }
}
