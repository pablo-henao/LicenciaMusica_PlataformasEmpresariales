package music.license.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import music.license.dto.auth.AuthResponse;
import music.license.dto.auth.LoginRequest;
import music.license.dto.auth.RegisterRequest;
import music.license.model.Usuario;
import music.license.repository.UsuarioRepository;
import music.license.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(RegisterRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());

        String passwordHash = passwordEncoder.encode(request.getPassword());

        usuario.setPasswordHash(passwordHash);
        usuario.setRol(request.getRol());

        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse validarLogin(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email o contrasena incorrectos"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPasswordHash())) {

            throw new BadCredentialsException("Email o contrasena incorrectos");
        }

        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponse(token, "Bearer");
    }
}
