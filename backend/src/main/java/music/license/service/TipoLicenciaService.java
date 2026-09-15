package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.licencia.TipoLicenciaRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Beat;
import music.license.model.TipoLicencia;
import music.license.model.Usuario;
import music.license.repository.BeatRepository;
import music.license.repository.TipoLicenciaRepository;
import music.license.security.AutorizacionUtil;

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

    public TipoLicencia crear(TipoLicenciaRequest request, Usuario solicitante) {
        Beat beat = beatRepository.findById(request.getBeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));

        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede definir sus licencias");

        TipoLicencia tipoLicencia = new TipoLicencia();
        tipoLicencia.setBeat(beat);
        tipoLicencia.setTipo(request.getTipo());
        tipoLicencia.setPrecio(request.getPrecio());
        tipoLicencia.setCondiciones(request.getCondiciones());

        return tipoLicenciaRepository.save(tipoLicencia);
    }

    public TipoLicencia actualizar(Long id, TipoLicenciaRequest datosActualizados, Usuario solicitante) {
        TipoLicencia tipoLicencia = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                tipoLicencia.getBeat().getProductor(),
                solicitante,
                "Solo el productor dueño del beat puede editar esta licencia");

        tipoLicencia.setTipo(datosActualizados.getTipo());
        tipoLicencia.setPrecio(datosActualizados.getPrecio());
        tipoLicencia.setCondiciones(datosActualizados.getCondiciones());
        // el beat asociado no se reasigna via PUT

        return tipoLicenciaRepository.save(tipoLicencia);
    }

    public void eliminar(Long id, Usuario solicitante) {
        TipoLicencia tipoLicencia = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                tipoLicencia.getBeat().getProductor(),
                solicitante,
                "Solo el productor dueño del beat puede eliminar esta licencia");

        tipoLicenciaRepository.deleteById(id);
    }
}
