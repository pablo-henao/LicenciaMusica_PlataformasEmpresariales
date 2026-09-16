package music.license.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.dto.acuerdo.AcuerdoCreditosResponse;
import music.license.dto.beat.BeatResponse;
import music.license.dto.common.PaginaResponse;
import music.license.dto.compra.CompraResponse;
import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.Compra;
import music.license.model.Usuario;
import music.license.service.AdminService;

/**
 * Vistas de solo lectura para el rol ADMIN. No hay endpoints de escritura a proposito:
 * ver docs/bloque-08-notificaciones-y-admin.md para el porque.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/beats")
    public PaginaResponse<BeatResponse> obtenerTodosLosBeats(
            @AuthenticationPrincipal Usuario usuario, @PageableDefault(size = 20) Pageable pageable) {

        Page<Beat> pagina = adminService.obtenerTodosLosBeats(usuario, pageable);
        return PaginaResponse.desde(pagina, BeatResponse::desde);
    }

    @GetMapping("/compras")
    public PaginaResponse<CompraResponse> obtenerTodasLasCompras(
            @AuthenticationPrincipal Usuario usuario, @PageableDefault(size = 20) Pageable pageable) {

        Page<Compra> pagina = adminService.obtenerTodasLasCompras(usuario, pageable);
        return PaginaResponse.desde(pagina, CompraResponse::desde);
    }

    @GetMapping("/acuerdos-creditos")
    public PaginaResponse<AcuerdoCreditosResponse> obtenerTodosLosAcuerdos(
            @AuthenticationPrincipal Usuario usuario, @PageableDefault(size = 20) Pageable pageable) {

        Page<AcuerdoCreditos> pagina = adminService.obtenerTodosLosAcuerdos(usuario, pageable);
        return PaginaResponse.desde(pagina, AcuerdoCreditosResponse::desde);
    }
}
