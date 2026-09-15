package music.license.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import music.license.dto.compra.CompraRequest;
import music.license.dto.compra.CompraResponse;
import music.license.model.Usuario;
import music.license.service.CompraService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<CompraResponse> obtenerTodas() {
        return compraService.obtenerTodas().stream()
                .map(CompraResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public CompraResponse obtenerPorId(@PathVariable Long id) {
        return CompraResponse.desde(compraService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse crear(@Valid @RequestBody CompraRequest request, @AuthenticationPrincipal Usuario usuario) {
        return CompraResponse.desde(compraService.crear(request, usuario));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        compraService.eliminar(id);
    }
}
