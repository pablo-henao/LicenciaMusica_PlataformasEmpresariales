package music.license.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import music.license.dto.colaborador.ColaboradorBeatRequest;
import music.license.dto.colaborador.ColaboradorBeatResponse;
import music.license.model.EstadoColaborador;
import music.license.model.Usuario;
import music.license.service.ColaboradorBeatService;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorBeatController {

    private final ColaboradorBeatService colaboradorBeatService;

    public ColaboradorBeatController(ColaboradorBeatService colaboradorBeatService) {
        this.colaboradorBeatService = colaboradorBeatService;
    }

    @GetMapping
    public List<ColaboradorBeatResponse> obtenerTodos(@AuthenticationPrincipal Usuario usuario) {
        return colaboradorBeatService.obtenerTodosVisibles(usuario).stream()
                .map(ColaboradorBeatResponse::desde)
                .toList();
    }

    /**
     * Las invitaciones a colaborar dirigidas al propio usuario autenticado.
     * Filtro opcional por estado, ej. ?estado=PENDIENTE para ver solo lo que falta responder.
     */
    @GetMapping("/mias")
    public List<ColaboradorBeatResponse> obtenerMisInvitaciones(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) EstadoColaborador estado) {

        return colaboradorBeatService.obtenerMisInvitaciones(usuario, estado).stream()
                .map(ColaboradorBeatResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public ColaboradorBeatResponse obtenerPorId(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ColaboradorBeatResponse.desde(colaboradorBeatService.obtenerPorIdVisible(id, usuario));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColaboradorBeatResponse crear(
            @Valid @RequestBody ColaboradorBeatRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        return ColaboradorBeatResponse.desde(colaboradorBeatService.crear(request, usuario));
    }

    @PutMapping("/{id}")
    public ColaboradorBeatResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ColaboradorBeatRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        return ColaboradorBeatResponse.desde(colaboradorBeatService.actualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/aceptar")
    public ColaboradorBeatResponse aceptar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ColaboradorBeatResponse.desde(colaboradorBeatService.aceptar(id, usuario));
    }

    @PatchMapping("/{id}/rechazar")
    public ColaboradorBeatResponse rechazar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ColaboradorBeatResponse.desde(colaboradorBeatService.rechazar(id, usuario));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        colaboradorBeatService.eliminar(id, usuario);
    }
}
