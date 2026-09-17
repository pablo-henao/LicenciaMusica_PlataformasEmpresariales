package music.license.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import music.license.dto.common.PaginaResponse;
import music.license.dto.compra.CompraRequest;
import music.license.dto.compra.CompraResponse;
import music.license.model.Compra;
import music.license.model.Usuario;
import music.license.service.CompraService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    /**
     * Historial de compras del comprador autenticado, mas recientes primero.
     */
    @GetMapping
    public PaginaResponse<CompraResponse> obtenerTodas(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Compra> pagina = compraService.obtenerTodas(usuario, pageable);
        return PaginaResponse.desde(pagina, CompraResponse::desde);
    }

    /**
     * El lado "venta": compras de licencias de los beats del propio productor.
     */
    @GetMapping("/mis-ventas")
    public PaginaResponse<CompraResponse> obtenerMisVentas(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Compra> pagina = compraService.obtenerMisVentas(usuario, pageable);
        return PaginaResponse.desde(pagina, CompraResponse::desde);
    }

    @GetMapping("/{id}")
    public CompraResponse obtenerPorId(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return CompraResponse.desde(compraService.obtenerPorId(id, usuario));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse crear(@Valid @RequestBody CompraRequest request, @AuthenticationPrincipal Usuario usuario) {
        return CompraResponse.desde(compraService.crear(request, usuario));
    }

    @PatchMapping("/{id}/checkout")
    public CompraResponse checkout(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return CompraResponse.desde(compraService.checkout(id, usuario));
    }

    @GetMapping("/{id}/contrato")
    public ResponseEntity<byte[]> descargarContrato(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        byte[] pdf = compraService.obtenerContrato(id, usuario);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contrato-compra-" + id + ".pdf\"")
                .body(pdf);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        compraService.eliminar(id, usuario);
    }
}
