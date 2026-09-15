package music.license.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import music.license.dto.acuerdo.AcuerdoCreditosRequest;
import music.license.dto.acuerdo.AcuerdoCreditosResponse;
import music.license.service.AcuerdoCreditosService;

@RestController
@RequestMapping("/api/acuerdos-creditos")
public class AcuerdoCreditosController {

    private final AcuerdoCreditosService acuerdoCreditosService;

    public AcuerdoCreditosController(AcuerdoCreditosService acuerdoCreditosService) {
        this.acuerdoCreditosService = acuerdoCreditosService;
    }

    @GetMapping
    public List<AcuerdoCreditosResponse> obtenerTodos() {
        return acuerdoCreditosService.obtenerTodos().stream()
                .map(AcuerdoCreditosResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public AcuerdoCreditosResponse obtenerPorId(@PathVariable Long id) {
        return AcuerdoCreditosResponse.desde(acuerdoCreditosService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcuerdoCreditosResponse crear(@Valid @RequestBody AcuerdoCreditosRequest request) {
        return AcuerdoCreditosResponse.desde(acuerdoCreditosService.crear(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        acuerdoCreditosService.eliminar(id);
    }
}
