package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.licencia.TipoLicenciaRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Beat;
import music.license.model.TipoLicencia;
import music.license.repository.BeatRepository;
import music.license.repository.TipoLicenciaRepository;

@Service
public class TipoLicenciaService {

    private final TipoLicenciaRepository tipoLicenciaRepository;
    private final BeatRepository beatRepository;

    public TipoLicenciaService(TipoLicenciaRepository tipoLicenciaRepository, BeatRepository beatRepository) {
        this.tipoLicenciaRepository = tipoLicenciaRepository;
        this.beatRepository = beatRepository;
    }

    public List<TipoLicencia> obtenerTodos() {
        return tipoLicenciaRepository.findAll();
    }

    public TipoLicencia obtenerPorId(Long id) {
        return tipoLicenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de licencia no encontrado"));
    }

    public TipoLicencia crear(TipoLicenciaRequest request) {
        Beat beat = beatRepository.findById(request.getBeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));

        TipoLicencia tipoLicencia = new TipoLicencia();
        tipoLicencia.setBeat(beat);
        tipoLicencia.setTipo(request.getTipo());
        tipoLicencia.setPrecio(request.getPrecio());
        tipoLicencia.setCondiciones(request.getCondiciones());

        return tipoLicenciaRepository.save(tipoLicencia);
    }

    public TipoLicencia actualizar(Long id, TipoLicenciaRequest datosActualizados) {
        TipoLicencia tipoLicencia = obtenerPorId(id);

        tipoLicencia.setTipo(datosActualizados.getTipo());
        tipoLicencia.setPrecio(datosActualizados.getPrecio());
        tipoLicencia.setCondiciones(datosActualizados.getCondiciones());
        // el beat asociado no se reasigna via PUT

        return tipoLicenciaRepository.save(tipoLicencia);
    }

    public void eliminar(Long id) {
        if (!tipoLicenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de licencia no encontrado");
        }

        tipoLicenciaRepository.deleteById(id);
    }
}
