package music.license.dto.usuario;

import music.license.model.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private String rol;

    public static UsuarioResponse desde(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.id = usuario.getId();
        dto.nombre = usuario.getNombre();
        dto.email = usuario.getEmail();
        dto.rol = usuario.getRol() != null ? usuario.getRol().name() : null;
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
