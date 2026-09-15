package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import music.license.dto.acuerdo.AcuerdoCreditosRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.EstadoAcuerdo;
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

    @BeforeEach
    void setUp() {
        beat = new Beat();
        beat.setId(1L);
    }

    @Test
    void crear_conBeatExistente_quedaAbiertoSinFechaDeCierre() {
        AcuerdoCreditosRequest request = new AcuerdoCreditosRequest();
        request.setBeatId(1L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(acuerdoCreditosRepository.save(any(AcuerdoCreditos.class))).thenAnswer(inv -> inv.getArgument(0));

        AcuerdoCreditos resultado = acuerdoCreditosService.crear(request);

        assertThat(resultado.getBeat()).isEqualTo(beat);
        assertThat(resultado.getEstado()).isEqualTo(EstadoAcuerdo.ABIERTO);
        assertThat(resultado.getFechaCierre()).isNull();
    }

    @Test
    void crear_conBeatInexistente_lanzaResourceNotFoundException() {
        AcuerdoCreditosRequest request = new AcuerdoCreditosRequest();
        request.setBeatId(99L);

        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> acuerdoCreditosService.crear(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
