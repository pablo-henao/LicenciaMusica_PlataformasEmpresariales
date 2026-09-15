package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.beat.BeatRequest;
import music.license.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));
    }

    public Beat guardar(Beat beat) {
        return beatRepository.save(beat);
    }

    public Beat actualizar(Long id, BeatRequest datosActualizados) {
        Beat beat = obtenerPorId(id);

        beat.setTitulo(datosActualizados.getTitulo());
        beat.setGenero(datosActualizados.getGenero());
        beat.setBpm(datosActualizados.getBpm());
        beat.setUrlPreview(datosActualizados.getUrlPreview());
        // productor y estado no se reasignan via PUT: el dueño no cambia por edicion,
        // y las transiciones de estado (publicar) tienen su propia regla de negocio

        return beatRepository.save(beat);
    }

    public void eliminar(Long id) {
        if (!beatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beat no encontrado");
        }

        beatRepository.deleteById(id);
    }
}
