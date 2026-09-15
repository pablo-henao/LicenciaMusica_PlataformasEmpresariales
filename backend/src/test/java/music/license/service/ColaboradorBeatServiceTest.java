package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import music.license.dto.colaborador.ColaboradorBeatRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Beat;
import music.license.model.ColaboradorBeat;
import music.license.model.EstadoColaborador;
import music.license.model.RolColaborador;
import music.license.model.Usuario;
import music.license.repository.BeatRepository;
import music.license.repository.ColaboradorBeatRepository;
import music.license.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ColaboradorBeatServiceTest {

    @Mock
    private ColaboradorBeatRepository colaboradorBeatRepository;

    @Mock
    private BeatRepository beatRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ColaboradorBeatService colaboradorBeatService;

    private Beat beat;
    private Usuario productor;
    private Usuario otroUsuario;
    private Usuario invitado;

    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setId(1L);

        otroUsuario = new Usuario();
        otroUsuario.setId(99L);

        beat = new Beat();
        beat.setId(1L);
        beat.setProductor(productor);

        invitado = new Usuario();
        invitado.setId(2L);
        invitado.setNombre("Vocalista Invitado");
    }

    @Test
    void crear_conDuenioDelBeat_quedaPendienteYPersiste() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(2L);
        request.setRol(RolColaborador.VOCALISTA);
        request.setPorcentajePropuesto(new BigDecimal("20"));

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(invitado));
        when(colaboradorBeatRepository.save(any(ColaboradorBeat.class))).thenAnswer(inv -> inv.getArgument(0));

        ColaboradorBeat resultado = colaboradorBeatService.crear(request, productor);

        assertThat(resultado.getBeat()).isEqualTo(beat);
        assertThat(resultado.getUsuario()).isEqualTo(invitado);
        assertThat(resultado.getEstado()).isEqualTo(EstadoColaborador.PENDIENTE);
    }

    @Test
    void crear_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(2L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        assertThatThrownBy(() -> colaboradorBeatService.crear(request, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(colaboradorBeatRepository, never()).save(any());
    }

    @Test
    void crear_conBeatInexistente_lanzaResourceNotFoundException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(99L);
        request.setUsuarioId(2L);

        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.crear(request, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crear_conUsuarioInvitadoInexistente_lanzaResourceNotFoundException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(99L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.crear(request, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminar_conDuenioDelBeat_borra() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));

        colaboradorBeatService.eliminar(5L, productor);

        verify(colaboradorBeatRepository).deleteById(5L);
    }

    @Test
    void eliminar_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));

        assertThatThrownBy(() -> colaboradorBeatService.eliminar(5L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(colaboradorBeatRepository, never()).deleteById(5L);
    }
}
