package music.license.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import music.license.dto.beat.BeatRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.ColaboradorBeat;
import music.license.model.EstadoAcuerdo;
import music.license.model.EstadoBeat;
import music.license.model.Rol;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;
import music.license.repository.ColaboradorBeatRepository;
import music.license.security.AutorizacionUtil;

@Service
public class BeatService {

    private final BeatRepository beatRepository;
    private final ColaboradorBeatRepository colaboradorBeatRepository;
    private final AcuerdoCreditosRepository acuerdoCreditosRepository;

    public BeatService(
            BeatRepository beatRepository,
            ColaboradorBeatRepository colaboradorBeatRepository,
            AcuerdoCreditosRepository acuerdoCreditosRepository) {

        this.beatRepository = beatRepository;
        this.colaboradorBeatRepository = colaboradorBeatRepository;
        this.acuerdoCreditosRepository = acuerdoCreditosRepository;
    }

    /**
     * El catalogo publico: solo beats PUBLICADO, con filtro opcional de genero/bpm y paginado.
     */
    public Page<Beat> buscarCatalogo(String genero, Integer bpmMin, Integer bpmMax, Pageable pageable) {
        return beatRepository.buscarCatalogo(EstadoBeat.PUBLICADO, genero, bpmMin, bpmMax, pageable);
    }

    /**
     * Los beats del propio productor, incluidos los que aun estan en BORRADOR.
     */
    public Page<Beat> obtenerMios(Usuario solicitante, Pageable pageable) {
        return beatRepository.findByProductorId(solicitante.getId(), pageable);
    }

    public Beat obtenerPorId(Long id) {
        return beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));
    }

    /**
     * Vista de detalle "publica": un beat en BORRADOR no existe para nadie que no sea su dueño
     * (se trata igual que un 404, para no confirmar ni exponer datos de un beat no publicado).
     */
    public Beat obtenerPorIdPublico(Long id, Usuario solicitante) {
        Beat beat = obtenerPorId(id);

        boolean esDuenio = solicitante != null
                && beat.getProductor() != null
                && beat.getProductor().getId().equals(solicitante.getId());

        if (beat.getEstado() != EstadoBeat.PUBLICADO && !esDuenio) {
            throw new ResourceNotFoundException("Beat no encontrado");
        }

        return beat;
    }

    public Beat guardar(Beat beat, Usuario solicitante) {
        AutorizacionUtil.exigirRol(solicitante, Rol.PRODUCTOR);

        return beatRepository.save(beat);
    }

    public Beat actualizar(Long id, BeatRequest datosActualizados, Usuario solicitante) {
        Beat beat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede editarlo");

        beat.setTitulo(datosActualizados.getTitulo());
        beat.setGenero(datosActualizados.getGenero());
        beat.setBpm(datosActualizados.getBpm());
        beat.setUrlPreview(datosActualizados.getUrlPreview());
        // productor y estado no se reasignan via PUT: el dueño no cambia por edicion,
        // y las transiciones de estado (publicar) tienen su propia regla de negocio

        return beatRepository.save(beat);
    }

    /**
     * Un beat solo (sin colaboradores declarados) se publica libremente.
     * Un beat con colaboradores necesita que su acuerdo de creditos este CERRADO,
     * es decir, que todos hayan aceptado y el split sume exactamente 100%.
     */
    public Beat publicar(Long id, Usuario solicitante) {
        Beat beat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede publicarlo");

        List<ColaboradorBeat> colaboradores = colaboradorBeatRepository.findByBeatId(id);

        if (!colaboradores.isEmpty()) {
            AcuerdoCreditos acuerdo = acuerdoCreditosRepository.findByBeatId(id)
                    .orElseThrow(() -> new IllegalStateException(
                            "Este beat tiene colaboradores declarados pero no tiene un acuerdo de créditos abierto"));

            if (acuerdo.getEstado() != EstadoAcuerdo.CERRADO) {
                throw new IllegalStateException(
                        "El acuerdo de créditos debe estar cerrado (100% aceptado por todos los colaboradores) antes de publicar");
            }
        }

        beat.setEstado(EstadoBeat.PUBLICADO);
        return beatRepository.save(beat);
    }

    public void eliminar(Long id, Usuario solicitante) {
        Beat beat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede eliminarlo");

        beatRepository.deleteById(id);
    }
}
