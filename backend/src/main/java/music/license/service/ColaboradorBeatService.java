package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.colaborador.ColaboradorBeatRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Beat;
import music.license.model.ColaboradorBeat;
import music.license.model.EstadoColaborador;
import music.license.model.Usuario;
import music.license.repository.BeatRepository;
import music.license.repository.ColaboradorBeatRepository;
import music.license.repository.UsuarioRepository;
import music.license.security.AutorizacionUtil;

@Service
public class ColaboradorBeatService {

    private final ColaboradorBeatRepository colaboradorBeatRepository;
    private final BeatRepository beatRepository;
    private final UsuarioRepository usuarioRepository;

    public ColaboradorBeatService(
            ColaboradorBeatRepository colaboradorBeatRepository,
            BeatRepository beatRepository,
            UsuarioRepository usuarioRepository) {

        this.colaboradorBeatRepository = colaboradorBeatRepository;
        this.beatRepository = beatRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ColaboradorBeat> obtenerTodos() {
        return colaboradorBeatRepository.findAll();
    }

    public ColaboradorBeat obtenerPorId(Long id) {
        return colaboradorBeatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador no encontrado"));
    }

    public ColaboradorBeat crear(ColaboradorBeatRequest request, Usuario solicitante) {
        Beat beat = beatRepository.findById(request.getBeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));

        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede invitar colaboradores");

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario colaborador no encontrado"));

        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(usuario);
        colaboradorBeat.setRol(request.getRol());
        colaboradorBeat.setPorcentajePropuesto(request.getPorcentajePropuesto());
        // toda invitacion nueva empieza pendiente: la aceptacion es un paso explicito del colaborador
        colaboradorBeat.setEstado(EstadoColaborador.PENDIENTE);

        return colaboradorBeatRepository.save(colaboradorBeat);
    }

    public void eliminar(Long id, Usuario solicitante) {
        ColaboradorBeat colaboradorBeat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                colaboradorBeat.getBeat().getProductor(),
                solicitante,
                "Solo el productor dueño del beat puede quitar colaboradores");

        colaboradorBeatRepository.deleteById(id);
    }
}
