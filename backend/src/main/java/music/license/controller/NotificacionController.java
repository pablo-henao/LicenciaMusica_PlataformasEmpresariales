package music.license.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.dto.common.PaginaResponse;
import music.license.dto.notificacion.NotificacionResponse;
import music.license.model.Notificacion;
import music.license.model.Usuario;
import music.license.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public PaginaResponse<NotificacionResponse> obtenerMias(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Notificacion> pagina = notificacionService.obtenerMias(usuario, pageable);
        return PaginaResponse.desde(pagina, NotificacionResponse::desde);
    }

    @GetMapping("/no-leidas/contador")
    public long contarNoLeidas(@AuthenticationPrincipal Usuario usuario) {
        return notificacionService.contarNoLeidas(usuario);
    }

    @PatchMapping("/{id}/leer")
    public NotificacionResponse marcarLeida(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return NotificacionResponse.desde(notificacionService.marcarLeida(id, usuario));
    }
}
