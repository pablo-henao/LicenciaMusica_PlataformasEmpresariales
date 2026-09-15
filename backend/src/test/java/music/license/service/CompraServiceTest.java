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

import music.license.dto.compra.CompraRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Compra;
import music.license.model.EstadoCompra;
import music.license.model.TipoLicencia;
import music.license.model.Usuario;
import music.license.repository.CompraRepository;
import music.license.repository.TipoLicenciaRepository;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private TipoLicenciaRepository tipoLicenciaRepository;

    @InjectMocks
    private CompraService compraService;

    private TipoLicencia tipoLicencia;
    private Usuario comprador;

    @BeforeEach
    void setUp() {
        tipoLicencia = new TipoLicencia();
        tipoLicencia.setId(1L);
        tipoLicencia.setPrecio(new BigDecimal("50000"));

        comprador = new Usuario();
        comprador.setId(2L);
    }

    @Test
    void crear_conTipoLicenciaExistente_quedaPendienteYAsociaComprador() {
        CompraRequest request = new CompraRequest();
        request.setTipoLicenciaId(1L);

        when(tipoLicenciaRepository.findById(1L)).thenReturn(Optional.of(tipoLicencia));
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        Compra resultado = compraService.crear(request, comprador);

        assertThat(resultado.getComprador()).isEqualTo(comprador);
        assertThat(resultado.getTipoLicencia()).isEqualTo(tipoLicencia);
        assertThat(resultado.getEstado()).isEqualTo(EstadoCompra.PENDIENTE);
        assertThat(resultado.getFecha()).isNotNull();
    }

    @Test
    void crear_conTipoLicenciaInexistente_lanzaResourceNotFoundException() {
        CompraRequest request = new CompraRequest();
        request.setTipoLicenciaId(99L);

        when(tipoLicenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> compraService.crear(request, comprador))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
