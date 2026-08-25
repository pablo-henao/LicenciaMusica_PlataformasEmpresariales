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
    @ResponseStatus(HttpStatus.CREATED)
    public TipoLicencia crear(@RequestBody TipoLicencia tipoLicencia) {
        return tipoLicenciaService.guardar(tipoLicencia);
    }

    @PutMapping("/{id}")
    public TipoLicencia actualizar(@PathVariable Long id, @RequestBody TipoLicencia tipoLicencia) {
        return tipoLicenciaService.actualizar(id, tipoLicencia);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        tipoLicenciaService.eliminar(id);
    }
}
