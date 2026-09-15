package music.license.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.compra.CompraRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Compra;
import music.license.model.EstadoBeat;
import music.license.model.EstadoCompra;
import music.license.model.TipoLicencia;
import music.license.model.Usuario;
import music.license.pdf.ContratoPdfGenerator;
import music.license.repository.CompraRepository;
import music.license.repository.TipoLicenciaRepository;
import music.license.security.AutorizacionUtil;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final TipoLicenciaRepository tipoLicenciaRepository;
    private final ContratoPdfGenerator contratoPdfGenerator;

    public CompraService(
            CompraRepository compraRepository,
            TipoLicenciaRepository tipoLicenciaRepository,
            ContratoPdfGenerator contratoPdfGenerator) {

        this.compraRepository = compraRepository;
        this.tipoLicenciaRepository = tipoLicenciaRepository;
        this.contratoPdfGenerator = contratoPdfGenerator;
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

        if (tipoLicencia.getBeat().getEstado() != EstadoBeat.PUBLICADO) {
            throw new IllegalStateException("Solo se pueden comprar licencias de beats publicados");
        }

        Compra compra = new Compra();
        compra.setComprador(comprador);
        compra.setTipoLicencia(tipoLicencia);
        compra.setFecha(LocalDateTime.now());
        // el paso a COMPLETADA (checkout + contrato en PDF) es un paso explicito aparte
        compra.setEstado(EstadoCompra.PENDIENTE);

        return compraRepository.save(compra);
    }

    /**
     * Completa la compra y emite el contrato en PDF con los terminos vigentes en
     * ese momento. El PDF queda guardado tal cual se genero: si despues cambia el
     * precio o las condiciones del TipoLicencia, el contrato ya emitido no cambia.
     */
    public Compra checkout(Long id, Usuario solicitante) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        AutorizacionUtil.exigirPropietario(
                compra.getComprador(), solicitante, "Solo el comprador puede completar esta compra");

        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new IllegalStateException("Esta compra ya fue completada anteriormente");
        }

        byte[] contrato = contratoPdfGenerator.generar(compra);
        compra.setContratoPdf(contrato);
        compra.setEstado(EstadoCompra.COMPLETADA);

        return compraRepository.save(compra);
    }

    public byte[] obtenerContrato(Long id, Usuario solicitante) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        AutorizacionUtil.exigirPropietario(
                compra.getComprador(), solicitante, "Solo el comprador puede descargar este contrato");

        if (compra.getEstado() != EstadoCompra.COMPLETADA || compra.getContratoPdf() == null) {
            throw new IllegalStateException("Esta compra todavía no tiene un contrato; complétala primero");
        }

        return compra.getContratoPdf();
    }

    public void eliminar(Long id, Usuario solicitante) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        AutorizacionUtil.exigirPropietario(
                compra.getComprador(), solicitante, "Solo el comprador puede eliminar esta compra");

        if (compra.getEstado() == EstadoCompra.COMPLETADA) {
            throw new IllegalStateException(
                    "No se puede eliminar una compra ya completada; el contrato ya fue emitido");
        }

        compraRepository.deleteById(id);
    }
}
