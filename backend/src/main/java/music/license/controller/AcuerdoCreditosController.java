package music.license.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.model.AcuerdoCreditos;
import music.license.service.AcuerdoCreditosService;

@RestController
@RequestMapping("/api/acuerdos-creditos")
public class AcuerdoCreditosController {

    private final AcuerdoCreditosService acuerdoCreditosService;

    public AcuerdoCreditosController(AcuerdoCreditosService acuerdoCreditosService) {
        this.acuerdoCreditosService = acuerdoCreditosService;
    }

    @GetMapping
    public List<AcuerdoCreditos> obtenerTodos() {
        return acuerdoCreditosService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public AcuerdoCreditos obtenerPorId(@PathVariable Long id) {
        return acuerdoCreditosService.obtenerPorId(id);
    }

    @PostMapping
    public AcuerdoCreditos crear(@RequestBody AcuerdoCreditos acuerdoCreditos) {
        return acuerdoCreditosService.guardar(acuerdoCreditos);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        acuerdoCreditosService.eliminar(id);
    }
}