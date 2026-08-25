package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public Beat actualizar(Long id, Beat datosActualizados) {
        Beat beat = obtenerPorId(id);

        beat.setTitulo(datosActualizados.getTitulo());
        beat.setGenero(datosActualizados.getGenero());
        beat.setBpm(datosActualizados.getBpm());
        beat.setUrlPreview(datosActualizados.getUrlPreview());
        beat.setEstado(datosActualizados.getEstado());
        // productor no se reasigna via PUT: el dueño de un beat no cambia por edicion

        return beatRepository.save(beat);
    }

    public void eliminar(Long id) {
        if (!beatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beat no encontrado");
        }

        beatRepository.deleteById(id);
    }
}
