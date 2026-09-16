package music.license.service;

import music.license.exception.ResourceNotFoundException;
import music.license.model.Usuario;
import music.license.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    /**
     * Busqueda acotada por email exacto (para invitar a un colaborador por su correo),
     * a proposito distinta de "listar todos los usuarios": no expone el directorio completo,
     * solo confirma/devuelve la persona que ya se busca puntualmente.
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con ese email"));
    }
}
