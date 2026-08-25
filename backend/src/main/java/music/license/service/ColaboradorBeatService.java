package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.exception.ResourceNotFoundException;
import music.license.model.ColaboradorBeat;
import music.license.repository.ColaboradorBeatRepository;

@Service
public class ColaboradorBeatService {

    private final ColaboradorBeatRepository colaboradorBeatRepository;

    public ColaboradorBeatService(ColaboradorBeatRepository colaboradorBeatRepository) {
        this.colaboradorBeatRepository = colaboradorBeatRepository;
    }

    public List<ColaboradorBeat> obtenerTodos() {
        return colaboradorBeatRepository.findAll();
    }

    public ColaboradorBeat obtenerPorId(Long id) {
        return colaboradorBeatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador no encontrado"));
    }

    public ColaboradorBeat guardar(ColaboradorBeat colaboradorBeat) {
        return colaboradorBeatRepository.save(colaboradorBeat);
    }

    public void eliminar(Long id) {
        if (!colaboradorBeatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Colaborador no encontrado");
        }

        colaboradorBeatRepository.deleteById(id);
    }
}