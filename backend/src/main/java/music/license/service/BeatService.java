package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.model.Beat;
import music.license.repository.BeatRepository;

@Service
public class BeatService {

    private final BeatRepository beatRepository;

    public BeatService(BeatRepository beatRepository) {
        this.beatRepository = beatRepository;
    }

    public List<Beat> obtenerTodos() {
        return beatRepository.findAll();
    }

    public Beat obtenerPorId(Long id) {
        return beatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beat no encontrado"));
    }

    public Beat guardar(Beat beat) {
        return beatRepository.save(beat);
    }

    public void eliminar(Long id) {
        if (!beatRepository.existsById(id)) {
            throw new RuntimeException("Beat no encontrado");
        }

        beatRepository.deleteById(id);
    }
}