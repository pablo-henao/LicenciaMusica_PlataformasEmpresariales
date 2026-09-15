package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.acuerdo.AcuerdoCreditosRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.EstadoAcuerdo;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;

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

    public AcuerdoCreditos crear(AcuerdoCreditosRequest request) {
        Beat beat = beatRepository.findById(request.getBeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));

        AcuerdoCreditos acuerdoCreditos = new AcuerdoCreditos();
        acuerdoCreditos.setBeat(beat);
        // todo acuerdo nuevo empieza abierto; el cierre lo dispara la aceptacion de todos los colaboradores
        acuerdoCreditos.setEstado(EstadoAcuerdo.ABIERTO);
        acuerdoCreditos.setFechaCierre(null);

        return acuerdoCreditosRepository.save(acuerdoCreditos);
    }

    public void eliminar(Long id) {
        if (!acuerdoCreditosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Acuerdo de créditos no encontrado");
        }

        acuerdoCreditosRepository.deleteById(id);
    }
}
