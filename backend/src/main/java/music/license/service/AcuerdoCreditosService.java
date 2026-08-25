package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.repository.AcuerdoCreditosRepository;

@Service
public class AcuerdoCreditosService {

    private final AcuerdoCreditosRepository acuerdoCreditosRepository;

    public AcuerdoCreditosService(AcuerdoCreditosRepository acuerdoCreditosRepository) {
        this.acuerdoCreditosRepository = acuerdoCreditosRepository;
    }

    public List<AcuerdoCreditos> obtenerTodos() {
        return acuerdoCreditosRepository.findAll();
    }

    public AcuerdoCreditos obtenerPorId(Long id) {
        return acuerdoCreditosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acuerdo de créditos no encontrado"));
    }

    public AcuerdoCreditos guardar(AcuerdoCreditos acuerdoCreditos) {
        return acuerdoCreditosRepository.save(acuerdoCreditos);
    }

    public void eliminar(Long id) {
        if (!acuerdoCreditosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Acuerdo de créditos no encontrado");
        }

        acuerdoCreditosRepository.deleteById(id);
    }
}