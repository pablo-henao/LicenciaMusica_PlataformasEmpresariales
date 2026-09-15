package music.license.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import music.license.dto.licencia.TipoLicenciaRequest;
import music.license.dto.licencia.TipoLicenciaResponse;
import music.license.service.TipoLicenciaService;

@RestController
@RequestMapping("/api/licencias")
public class TipoLicenciaController {

    private final TipoLicenciaService tipoLicenciaService;

    public TipoLicenciaController(TipoLicenciaService tipoLicenciaService) {
        this.tipoLicenciaService = tipoLicenciaService;
    }

    @GetMapping
    public List<TipoLicenciaResponse> obtenerTodos() {
        return tipoLicenciaService.obtenerTodos().stream()
                .map(TipoLicenciaResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public TipoLicenciaResponse obtenerPorId(@PathVariable Long id) {
        return TipoLicenciaResponse.desde(tipoLicenciaService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoLicenciaResponse crear(@Valid @RequestBody TipoLicenciaRequest request) {
        return TipoLicenciaResponse.desde(tipoLicenciaService.crear(request));
    }

    @PutMapping("/{id}")
    public TipoLicenciaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TipoLicenciaRequest request) {
        return TipoLicenciaResponse.desde(tipoLicenciaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        tipoLicenciaService.eliminar(id);
    }
}
