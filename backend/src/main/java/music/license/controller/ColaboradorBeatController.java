package music.license.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.model.ColaboradorBeat;
import music.license.service.ColaboradorBeatService;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorBeatController {

    private final ColaboradorBeatService colaboradorBeatService;

    public ColaboradorBeatController(ColaboradorBeatService colaboradorBeatService) {
        this.colaboradorBeatService = colaboradorBeatService;
    }

    @GetMapping
    public List<ColaboradorBeat> obtenerTodos() {
        return colaboradorBeatService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ColaboradorBeat obtenerPorId(@PathVariable Long id) {
        return colaboradorBeatService.obtenerPorId(id);
    }

    @PostMapping
    public ColaboradorBeat crear(@RequestBody ColaboradorBeat colaboradorBeat) {
        return colaboradorBeatService.guardar(colaboradorBeat);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        colaboradorBeatService.eliminar(id);
    }
}