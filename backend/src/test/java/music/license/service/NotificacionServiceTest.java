package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import music.license.exception.ResourceNotFoundException;
import music.license.model.Notificacion;
import music.license.model.TipoNotificacion;
import music.license.model.Usuario;
import music.license.repository.NotificacionRepository;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Usuario destinatario;
    private Usuario otroUsuario;

    @BeforeEach
    void setUp() {
        destinatario = new Usuario();
        destinatario.setId(1L);

        otroUsuario = new Usuario();
        otroUsuario.setId(99L);
    }

    @Test
    void crear_guardaLaNotificacionSinLeer() {
        notificacionService.crear(destinatario, TipoNotificacion.INVITACION_COLABORACION, "Te invitaron a colaborar");

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertThat(guardada.getUsuario()).isEqualTo(destinatario);
        assertThat(guardada.getTipo()).isEqualTo(TipoNotificacion.INVITACION_COLABORACION);
        assertThat(guardada.getMensaje()).isEqualTo("Te invitaron a colaborar");
        assertThat(guardada.isLeida()).isFalse();
        assertThat(guardada.getFecha()).isNotNull();
    }

    @Test
    void obtenerMias_delegaEnElRepositorioPorUsuario() {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(destinatario);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Notificacion> pagina = new PageImpl<>(List.of(notificacion));

        when(notificacionRepository.findByUsuarioIdOrderByFechaDesc(1L, pageable)).thenReturn(pagina);

        Page<Notificacion> resultado = notificacionService.obtenerMias(destinatario, pageable);

        assertThat(resultado.getContent()).containsExactly(notificacion);
    }

    @Test
    void contarNoLeidas_delegaEnElRepositorio() {
        when(notificacionRepository.countByUsuarioIdAndLeidaFalse(1L)).thenReturn(3L);

        long resultado = notificacionService.contarNoLeidas(destinatario);

        assertThat(resultado).isEqualTo(3L);
    }

    @Test
    void marcarLeida_conDestinatario_quedaLeida() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setUsuario(destinatario);
        notificacion.setLeida(false);

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(notificacion)).thenReturn(notificacion);

        Notificacion resultado = notificacionService.marcarLeida(1L, destinatario);

        assertThat(resultado.isLeida()).isTrue();
    }

    @Test
    void marcarLeida_conUsuarioQueNoEsElDestinatario_lanzaAccessDeniedException() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setUsuario(destinatario);

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        assertThatThrownBy(() -> notificacionService.marcarLeida(1L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void marcarLeida_conIdInexistente_lanzaResourceNotFoundException() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacionService.marcarLeida(99L, destinatario))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
