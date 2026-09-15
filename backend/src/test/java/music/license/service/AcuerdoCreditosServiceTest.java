package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import music.license.dto.acuerdo.AcuerdoCreditosRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.EstadoAcuerdo;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;

@ExtendWith(MockitoExtension.class)
class AcuerdoCreditosServiceTest {

    @Mock
    private AcuerdoCreditosRepository acuerdoCreditosRepository;

    @Mock
    private BeatRepository beatRepository;

    @InjectMocks
    private AcuerdoCreditosService acuerdoCreditosService;

    private Beat beat;
    private Usuario productor;
    private Usuario otroUsuario;

    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setId(1L);

        otroUsuario = new Usuario();
        otroUsuario.setId(99L);

        beat = new Beat();
        beat.setId(1L);
        beat.setProductor(productor);
    }

    @Test
    void crear_conDuenioDelBeat_quedaAbiertoSinFechaDeCierre() {
        AcuerdoCreditosRequest request = new AcuerdoCreditosRequest();
        request.setBeatId(1L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(acuerdoCreditosRepository.save(any(AcuerdoCreditos.class))).thenAnswer(inv -> inv.getArgument(0));

        AcuerdoCreditos resultado = acuerdoCreditosService.crear(request, productor);

        assertThat(resultado.getBeat()).isEqualTo(beat);
        assertThat(resultado.getEstado()).isEqualTo(EstadoAcuerdo.ABIERTO);
        assertThat(resultado.getFechaCierre()).isNull();
    }

    @Test
    void crear_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        AcuerdoCreditosRequest request = new AcuerdoCreditosRequest();
        request.setBeatId(1L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        assertThatThrownBy(() -> acuerdoCreditosService.crear(request, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(acuerdoCreditosRepository, never()).save(any());
    }

    @Test
    void crear_conBeatInexistente_lanzaResourceNotFoundException() {
        AcuerdoCreditosRequest request = new AcuerdoCreditosRequest();
        request.setBeatId(99L);

        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> acuerdoCreditosService.crear(request, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminar_conDuenioDelBeat_borra() {
        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setId(3L);
        acuerdo.setBeat(beat);

        when(acuerdoCreditosRepository.findById(3L)).thenReturn(Optional.of(acuerdo));

        acuerdoCreditosService.eliminar(3L, productor);

        verify(acuerdoCreditosRepository).deleteById(3L);
    }

    @Test
    void eliminar_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setId(3L);
        acuerdo.setBeat(beat);

        when(acuerdoCreditosRepository.findById(3L)).thenReturn(Optional.of(acuerdo));

        assertThatThrownBy(() -> acuerdoCreditosService.eliminar(3L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(acuerdoCreditosRepository, never()).deleteById(3L);
    }
}
