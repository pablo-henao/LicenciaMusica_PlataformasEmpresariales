package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        beat = new Beat();
        beat.setId(1L);

        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNombre("Vocalista Invitado");
    }

    @Test
    void crear_conBeatYUsuarioExistentes_quedaPendienteYPersiste() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(2L);
        request.setRol(RolColaborador.VOCALISTA);
        request.setPorcentajePropuesto(new BigDecimal("20"));

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(colaboradorBeatRepository.save(any(ColaboradorBeat.class))).thenAnswer(inv -> inv.getArgument(0));

        ColaboradorBeat resultado = colaboradorBeatService.crear(request);

        assertThat(resultado.getBeat()).isEqualTo(beat);
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getEstado()).isEqualTo(EstadoColaborador.PENDIENTE);
    }

    @Test
    void crear_conBeatInexistente_lanzaResourceNotFoundException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(99L);
        request.setUsuarioId(2L);

        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.crear(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crear_conUsuarioInexistente_lanzaResourceNotFoundException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(99L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.crear(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
