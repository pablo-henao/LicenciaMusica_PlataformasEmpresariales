package music.license.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import music.license.dto.compra.CompraRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Compra;
import music.license.model.EstadoBeat;
import music.license.model.EstadoCompra;
import music.license.model.TipoLicencia;
import music.license.model.TipoNotificacion;
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
    private final NotificacionService notificacionService;

    public CompraService(
            CompraRepository compraRepository,
            TipoLicenciaRepository tipoLicenciaRepository,
            ContratoPdfGenerator contratoPdfGenerator,
            NotificacionService notificacionService) {

        this.compraRepository = compraRepository;
        this.tipoLicenciaRepository = tipoLicenciaRepository;
        this.contratoPdfGenerator = contratoPdfGenerator;
        this.notificacionService = notificacionService;
    }

    /**
     * Historial de compras del propio usuario, paginado. No existe un rol admin
     * que justifique ver las compras de todos los usuarios.
     */
    public Page<Compra> obtenerTodas(Usuario solicitante, Pageable pageable) {
        return compraRepository.findByCompradorId(solicitante.getId(), pageable);
    }

    /**
     * Las compras de licencias de los beats del propio productor (el lado "venta"
     * de la transaccion, en vez del lado "compra" que ya cubre obtenerTodas).
     */
    public Page<Compra> obtenerMisVentas(Usuario solicitante, Pageable pageable) {
        return compraRepository.findByProductorId(solicitante.getId(), pageable);
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

        Compra guardada = compraRepository.save(compra);

        String tituloBeat = compra.getTipoLicencia().getBeat().getTitulo();

        notificacionService.crear(compra.getComprador(), TipoNotificacion.COMPRA_COMPLETADA,
                "Tu compra de la licencia de \"" + tituloBeat + "\" se completó. Ya puedes descargar el contrato");

        notificacionService.crear(compra.getTipoLicencia().getBeat().getProductor(), TipoNotificacion.COMPRA_COMPLETADA,
                compra.getComprador().getNombre() + " compró una licencia de \"" + tituloBeat + "\"");

        return guardada;
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
