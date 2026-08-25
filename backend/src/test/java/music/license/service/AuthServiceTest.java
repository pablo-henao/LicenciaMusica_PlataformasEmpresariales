package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import music.license.dto.auth.AuthResponse;
import music.license.dto.auth.LoginRequest;
import music.license.dto.auth.RegisterRequest;
import music.license.model.Rol;
import music.license.model.Usuario;
import music.license.repository.UsuarioRepository;
import music.license.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_conEmailNuevo_creaUsuarioYDevuelveToken() {
        RegisterRequest request = new RegisterRequest("Juan", "juan@upb.edu.co", "clave123", Rol.PRODUCTOR);

        when(usuarioRepository.existsByEmail("juan@upb.edu.co")).thenReturn(false);
        when(passwordEncoder.encode("clave123")).thenReturn("hash-simulado");
        when(jwtService.generarToken("juan@upb.edu.co")).thenReturn("token-simulado");

        AuthResponse respuesta = authService.registrar(request);

        assertThat(respuesta.getToken()).isEqualTo("token-simulado");
        assertThat(respuesta.getTipo()).isEqualTo("Bearer");
    }

    @Test
    void registrar_conEmailYaRegistrado_lanzaIllegalArgumentException() {
        RegisterRequest request = new RegisterRequest("Juan", "juan@upb.edu.co", "clave123", Rol.PRODUCTOR);

        when(usuarioRepository.existsByEmail("juan@upb.edu.co")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya esta registrado");
    }

    @Test
    void login_conCredencialesCorrectas_devuelveToken() {
        LoginRequest request = new LoginRequest("juan@upb.edu.co", "clave123");

        Usuario usuario = new Usuario();
        usuario.setEmail("juan@upb.edu.co");
        usuario.setPasswordHash("hash-guardado");

        when(usuarioRepository.findByEmail("juan@upb.edu.co")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123", "hash-guardado")).thenReturn(true);
        when(jwtService.generarToken("juan@upb.edu.co")).thenReturn("token-simulado");

        AuthResponse respuesta = authService.validarLogin(request);

        assertThat(respuesta.getToken()).isEqualTo("token-simulado");
    }

    @Test
    void login_conPasswordIncorrecta_lanzaBadCredentialsException() {
        LoginRequest request = new LoginRequest("juan@upb.edu.co", "claveMala");

        Usuario usuario = new Usuario();
        usuario.setEmail("juan@upb.edu.co");
        usuario.setPasswordHash("hash-guardado");

        when(usuarioRepository.findByEmail("juan@upb.edu.co")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("claveMala", "hash-guardado")).thenReturn(false);

        assertThatThrownBy(() -> authService.validarLogin(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_conEmailInexistente_lanzaBadCredentialsException() {
        LoginRequest request = new LoginRequest("noexiste@upb.edu.co", "clave123");

        when(usuarioRepository.findByEmail("noexiste@upb.edu.co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.validarLogin(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
