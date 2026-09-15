package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import music.license.dto.licencia.TipoLicenciaRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Beat;
import music.license.model.TipoLicencia;
import music.license.model.TipoLicenciaEnum;
import music.license.model.Usuario;
import music.license.repository.BeatRepository;
import music.license.repository.TipoLicenciaRepository;

@ExtendWith(MockitoExtension.class)
class TipoLicenciaServiceTest {

    @Mock
    private TipoLicenciaRepository tipoLicenciaRepository;

    @Mock
    private BeatRepository beatRepository;

    @InjectMocks
    private TipoLicenciaService tipoLicenciaService;

    private TipoLicencia licencia;
    private Beat beat;
    private Usuario productor;
    private Usuario otroUsuario;

    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setId(10L);

        otroUsuario = new Usuario();
        otroUsuario.setId(20L);

        beat = new Beat();
        beat.setId(1L);
        beat.setTitulo("Sueños de Medallo");
        beat.setProductor(productor);

        licencia = new TipoLicencia();
        licencia.setId(1L);
        licencia.setBeat(beat);
        licencia.setTipo(TipoLicenciaEnum.NO_EXCLUSIVA);
        licencia.setPrecio(new BigDecimal("50000"));
        licencia.setCondiciones("Uso no comercial");
    }

    @Test
    void obtenerTodos_devuelveTodasLasLicencias() {
        when(tipoLicenciaRepository.findAll()).thenReturn(List.of(licencia));

        List<TipoLicencia> resultado = tipoLicenciaService.obtenerTodos();

        assertThat(resultado).containsExactly(licencia);
    }

    @Test
    void obtenerPorId_conIdExistente_devuelveLicencia() {
        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));

        TipoLicencia resultado = tipoLicenciaService.obtenerPorId(1L);

        assertThat(resultado.getPrecio()).isEqualByComparingTo("50000");
    }

    @Test
    void obtenerPorId_conIdInexistente_lanzaExcepcion() {
        when(tipoLicenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLicenciaService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crear_conDuenioDelBeat_asociaElBeatYPersiste() {
        TipoLicenciaRequest request = new TipoLicenciaRequest();
        request.setBeatId(1L);
        request.setTipo(TipoLicenciaEnum.EXCLUSIVA);
        request.setPrecio(new BigDecimal("200000"));
        request.setCondiciones("Derechos exclusivos");

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(tipoLicenciaRepository.save(any(TipoLicencia.class))).thenAnswer(inv -> inv.getArgument(0));

        TipoLicencia resultado = tipoLicenciaService.crear(request, productor);

        assertThat(resultado.getBeat()).isEqualTo(beat);
        assertThat(resultado.getTipo()).isEqualTo(TipoLicenciaEnum.EXCLUSIVA);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("200000");
    }

    @Test
    void crear_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        TipoLicenciaRequest request = new TipoLicenciaRequest();
        request.setBeatId(1L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        assertThatThrownBy(() -> tipoLicenciaService.crear(request, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(tipoLicenciaRepository, never()).save(any());
    }

    @Test
    void crear_conBeatInexistente_lanzaResourceNotFoundException() {
        TipoLicenciaRequest request = new TipoLicenciaRequest();
        request.setBeatId(99L);

        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLicenciaService.crear(request, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void actualizar_conDuenioDelBeat_modificaTipoPrecioYCondiciones() {
        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));
        when(tipoLicenciaRepository.save(licencia)).thenReturn(licencia);

        TipoLicenciaRequest nuevosDatos = new TipoLicenciaRequest();
        nuevosDatos.setTipo(TipoLicenciaEnum.EXCLUSIVA);
        nuevosDatos.setPrecio(new BigDecimal("300000"));
        nuevosDatos.setCondiciones("Uso comercial completo, derechos exclusivos");

        TipoLicencia resultado = tipoLicenciaService.actualizar(1L, nuevosDatos, productor);

        assertThat(resultado.getTipo()).isEqualTo(TipoLicenciaEnum.EXCLUSIVA);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("300000");
        assertThat(resultado.getBeat()).isEqualTo(beat);
    }

    @Test
    void actualizar_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));

        assertThatThrownBy(() -> tipoLicenciaService.actualizar(1L, new TipoLicenciaRequest(), otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(tipoLicenciaRepository, never()).save(any());
    }

    @Test
    void eliminar_conDuenioDelBeat_borra() {
        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));

        tipoLicenciaService.eliminar(1L, productor);

        verify(tipoLicenciaRepository).deleteById(1L);
    }

    @Test
    void eliminar_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));

        assertThatThrownBy(() -> tipoLicenciaService.eliminar(1L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(tipoLicenciaRepository, never()).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_lanzaExcepcion() {
        when(tipoLicenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLicenciaService.eliminar(99L, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
