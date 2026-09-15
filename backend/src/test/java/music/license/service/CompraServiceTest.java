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
    private Usuario otroUsuario;

    @BeforeEach
    void setUp() {
        tipoLicencia = new TipoLicencia();
        tipoLicencia.setId(1L);
        tipoLicencia.setPrecio(new BigDecimal("50000"));

        comprador = new Usuario();
        comprador.setId(2L);

        otroUsuario = new Usuario();
        otroUsuario.setId(99L);
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

    @Test
    void obtenerTodas_devuelveSoloLasComprasDelSolicitante() {
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setComprador(comprador);

        when(compraRepository.findByCompradorId(2L)).thenReturn(List.of(compra));

        List<Compra> resultado = compraService.obtenerTodas(comprador);

        assertThat(resultado).containsExactly(compra);
    }

    @Test
    void obtenerPorId_conDuenio_devuelveLaCompra() {
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setComprador(comprador);

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        Compra resultado = compraService.obtenerPorId(1L, comprador);

        assertThat(resultado).isEqualTo(compra);
    }

    @Test
    void obtenerPorId_conUsuarioQueNoEsElComprador_lanzaAccessDeniedException() {
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setComprador(comprador);

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        assertThatThrownBy(() -> compraService.obtenerPorId(1L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void eliminar_conDuenio_borra() {
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setComprador(comprador);

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        compraService.eliminar(1L, comprador);

        verify(compraRepository).deleteById(1L);
    }

    @Test
    void eliminar_conUsuarioQueNoEsElComprador_lanzaAccessDeniedException() {
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setComprador(comprador);

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        assertThatThrownBy(() -> compraService.eliminar(1L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(compraRepository, never()).deleteById(1L);
    }
}
