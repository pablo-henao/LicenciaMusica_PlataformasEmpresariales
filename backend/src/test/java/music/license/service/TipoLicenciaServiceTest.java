package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import music.license.exception.ResourceNotFoundException;
import music.license.model.TipoLicencia;
import music.license.model.TipoLicenciaEnum;
import music.license.repository.TipoLicenciaRepository;

@ExtendWith(MockitoExtension.class)
class TipoLicenciaServiceTest {

    @Mock
    private TipoLicenciaRepository tipoLicenciaRepository;

    @InjectMocks
    private TipoLicenciaService tipoLicenciaService;

    private TipoLicencia licencia;

    @BeforeEach
    void setUp() {
        licencia = new TipoLicencia();
        licencia.setId(1L);
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
    void actualizar_modificaTipoPrecioYCondiciones() {
        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));
        when(tipoLicenciaRepository.save(licencia)).thenReturn(licencia);

        TipoLicencia nuevosDatos = new TipoLicencia();
        nuevosDatos.setTipo(TipoLicenciaEnum.EXCLUSIVA);
        nuevosDatos.setPrecio(new BigDecimal("300000"));
        nuevosDatos.setCondiciones("Uso comercial completo, derechos exclusivos");

        TipoLicencia resultado = tipoLicenciaService.actualizar(1L, nuevosDatos);

        assertThat(resultado.getTipo()).isEqualTo(TipoLicenciaEnum.EXCLUSIVA);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("300000");
    }

    @Test
    void eliminar_conIdExistente_borra() {
        when(tipoLicenciaRepository.existsById(1L)).thenReturn(true);

        tipoLicenciaService.eliminar(1L);

        verify(tipoLicenciaRepository).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_lanzaExcepcion() {
        when(tipoLicenciaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tipoLicenciaService.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
