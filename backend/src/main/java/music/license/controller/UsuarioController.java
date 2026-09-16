package music.license.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import music.license.dto.usuario.UsuarioResponse;
import music.license.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtenerPorId(@PathVariable Long id) {
        return UsuarioResponse.desde(usuarioService.obtenerPorId(id));
    }

    /**
     * Busqueda puntual por email exacto (para invitar a un colaborador conociendo su correo).
     * A proposito no existe un GET que liste todos los usuarios: eso exponia el email de
     * cualquiera a cualquier usuario autenticado.
     */
    @GetMapping("/buscar")
    public UsuarioResponse buscarPorEmail(@RequestParam String email) {
        return UsuarioResponse.desde(usuarioService.buscarPorEmail(email));
    }
}
