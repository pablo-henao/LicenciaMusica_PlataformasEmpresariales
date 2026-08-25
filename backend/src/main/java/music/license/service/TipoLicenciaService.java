package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.exception.ResourceNotFoundException;
import music.license.model.TipoLicencia;
import music.license.repository.TipoLicenciaRepository;

@Service
public class TipoLicenciaService {

    private final TipoLicenciaRepository tipoLicenciaRepository;

    public TipoLicenciaService(TipoLicenciaRepository tipoLicenciaRepository) {
        this.tipoLicenciaRepository = tipoLicenciaRepository;
    }

    public List<TipoLicencia> obtenerTodos() {
        return tipoLicenciaRepository.findAll();
    }

    public TipoLicencia obtenerPorId(Long id) {
        return tipoLicenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de licencia no encontrado"));
    }

    public TipoLicencia guardar(TipoLicencia tipoLicencia) {
        return tipoLicenciaRepository.save(tipoLicencia);
    }

    public TipoLicencia actualizar(Long id, TipoLicencia datosActualizados) {
        TipoLicencia tipoLicencia = obtenerPorId(id);

        tipoLicencia.setTipo(datosActualizados.getTipo());
        tipoLicencia.setPrecio(datosActualizados.getPrecio());
        tipoLicencia.setCondiciones(datosActualizados.getCondiciones());

        return tipoLicenciaRepository.save(tipoLicencia);
    }

    public void eliminar(Long id) {
        if (!tipoLicenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de licencia no encontrado");
        }

        tipoLicenciaRepository.deleteById(id);
    }
}
