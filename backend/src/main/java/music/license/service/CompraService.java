package music.license.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.compra.CompraRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Compra;
import music.license.model.EstadoCompra;
import music.license.model.TipoLicencia;
import music.license.model.Usuario;
import music.license.repository.CompraRepository;
import music.license.repository.TipoLicenciaRepository;
import music.license.security.AutorizacionUtil;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final TipoLicenciaRepository tipoLicenciaRepository;

    public CompraService(CompraRepository compraRepository, TipoLicenciaRepository tipoLicenciaRepository) {
        this.compraRepository = compraRepository;
        this.tipoLicenciaRepository = tipoLicenciaRepository;
    }

    /**
     * De momento equivale a "mis compras": no existe un rol admin que justifique
     * ver las compras de todos los usuarios. El bloque de catalogo/historial
     * puede ampliar esto con paginacion y una vista para el productor vendedor.
     */
    public List<Compra> obtenerTodas(Usuario solicitante) {
        return compraRepository.findByCompradorId(solicitante.getId());
    }

    public Compra obtenerPorId(Long id, Usuario solicitante) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        AutorizacionUtil.exigirPropietario(
                compra.getComprador(), solicitante, "Solo el comprador puede ver esta compra");

        return compra;
    }

    public Compra crear(CompraRequest request, Usuario comprador) {
        TipoLicencia tipoLicencia = tipoLicenciaRepository.findById(request.getTipoLicenciaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de licencia no encontrado"));

        Compra compra = new Compra();
        compra.setComprador(comprador);
        compra.setTipoLicencia(tipoLicencia);
        compra.setFecha(LocalDateTime.now());
        // el paso a COMPLETADA (pago + contrato en PDF) es responsabilidad del flujo de checkout
        compra.setEstado(EstadoCompra.PENDIENTE);

        return compraRepository.save(compra);
    }

    public void eliminar(Long id, Usuario solicitante) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        AutorizacionUtil.exigirPropietario(
                compra.getComprador(), solicitante, "Solo el comprador puede eliminar esta compra");

        compraRepository.deleteById(id);
    }
}
