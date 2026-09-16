package music.license.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.Compra;
import music.license.model.Rol;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;
import music.license.repository.CompraRepository;
import music.license.security.AutorizacionUtil;

/**
 * Vista de administrador de solo lectura: ver todo el catalogo (incluidos borradores),
 * todas las compras y todos los acuerdos de creditos, sin filtrar por dueño. A proposito
 * NO tiene metodos de escritura: un admin no edita/borra recursos ajenos, eso seguiria
 * rompiendo las reglas de dueño que ya usa el resto del sistema (AutorizacionUtil).
 */
@Service
public class AdminService {

    private final BeatRepository beatRepository;
    private final CompraRepository compraRepository;
    private final AcuerdoCreditosRepository acuerdoCreditosRepository;

    public AdminService(
            BeatRepository beatRepository,
            CompraRepository compraRepository,
            AcuerdoCreditosRepository acuerdoCreditosRepository) {

        this.beatRepository = beatRepository;
        this.compraRepository = compraRepository;
        this.acuerdoCreditosRepository = acuerdoCreditosRepository;
    }

    public Page<Beat> obtenerTodosLosBeats(Usuario solicitante, Pageable pageable) {
        AutorizacionUtil.exigirRol(solicitante, Rol.ADMIN);

        return beatRepository.findAll(pageable);
    }

    public Page<Compra> obtenerTodasLasCompras(Usuario solicitante, Pageable pageable) {
        AutorizacionUtil.exigirRol(solicitante, Rol.ADMIN);

        return compraRepository.findAll(pageable);
    }

    public Page<AcuerdoCreditos> obtenerTodosLosAcuerdos(Usuario solicitante, Pageable pageable) {
        AutorizacionUtil.exigirRol(solicitante, Rol.ADMIN);

        return acuerdoCreditosRepository.findAll(pageable);
    }
}
