package music.license.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.model.TipoLicencia;
import music.license.service.TipoLicenciaService;

@RestController
@RequestMapping("/api/licencias")
public class TipoLicenciaController {

    private final TipoLicenciaService tipoLicenciaService;

    public TipoLicenciaController(TipoLicenciaService tipoLicenciaService) {
        this.tipoLicenciaService = tipoLicenciaService;
    }

    @GetMapping
    public List<TipoLicencia> obtenerTodos() {
        return tipoLicenciaService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public TipoLicencia obtenerPorId(@PathVariable Long id) {
        return tipoLicenciaService.obtenerPorId(id);
    }

    @PostMapping
    public TipoLicencia crear(@RequestBody TipoLicencia tipoLicencia) {
        return tipoLicenciaService.guardar(tipoLicencia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tipoLicenciaService.eliminar(id);
    }
}