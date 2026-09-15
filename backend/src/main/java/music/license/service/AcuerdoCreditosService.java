package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.acuerdo.AcuerdoCreditosRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.EstadoAcuerdo;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;
import music.license.security.AutorizacionUtil;

@Service
public class AcuerdoCreditosService {

    private final AcuerdoCreditosRepository acuerdoCreditosRepository;
    private final BeatRepository beatRepository;

    public AcuerdoCreditosService(AcuerdoCreditosRepository acuerdoCreditosRepository, BeatRepository beatRepository) {
        this.acuerdoCreditosRepository = acuerdoCreditosRepository;
        this.beatRepository = beatRepository;
    }

    public List<AcuerdoCreditos> obtenerTodos() {
        return acuerdoCreditosRepository.findAll();
    }

    public AcuerdoCreditos obtenerPorId(Long id) {
        return acuerdoCreditosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acuerdo de créditos no encontrado"));
    }

    public AcuerdoCreditos crear(AcuerdoCreditosRequest request, Usuario solicitante) {
        Beat beat = beatRepository.findById(request.getBeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));

        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede abrir el acuerdo de créditos");

        AcuerdoCreditos acuerdoCreditos = new AcuerdoCreditos();
        acuerdoCreditos.setBeat(beat);
        // todo acuerdo nuevo empieza abierto; el cierre lo dispara la aceptacion de todos los colaboradores
        acuerdoCreditos.setEstado(EstadoAcuerdo.ABIERTO);
        acuerdoCreditos.setFechaCierre(null);

        return acuerdoCreditosRepository.save(acuerdoCreditos);
    }

    public void eliminar(Long id, Usuario solicitante) {
        AcuerdoCreditos acuerdoCreditos = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                acuerdoCreditos.getBeat().getProductor(),
                solicitante,
                "Solo el productor dueño del beat puede eliminar el acuerdo de créditos");

        if (acuerdoCreditos.getEstado() == EstadoAcuerdo.CERRADO) {
            throw new IllegalStateException(
                    "No se puede eliminar un acuerdo de créditos ya cerrado; agrega, edita o quita un colaborador para reabrirlo");
        }

        acuerdoCreditosRepository.deleteById(id);
    }
}
