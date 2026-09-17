package music.license.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import music.license.dto.auth.AuthResponse;
import music.license.dto.auth.LoginRequest;
import music.license.dto.auth.RegisterRequest;
import music.license.dto.usuario.UsuarioResponse;
import music.license.model.Usuario;
import music.license.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrar(@RequestBody RegisterRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.validarLogin(request);
    }

    /**
     * Resuelve el usuario autenticado a partir del JWT (el token solo trae el email
     * como subject; el frontend necesita el id/nombre/rol despues de loguearse o al
     * refrescar la pagina, sin tener que recordar el email por su cuenta).
     */
    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal Usuario usuario) {
        return UsuarioResponse.desde(usuario);
    }
}
