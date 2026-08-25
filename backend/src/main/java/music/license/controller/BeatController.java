package music.license.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.model.Beat;
import music.license.service.BeatService;

@RestController
@RequestMapping("/api/beats")
public class BeatController {

    private final BeatService beatService;

    public BeatController(BeatService beatService) {
        this.beatService = beatService;
    }

    @GetMapping
    public List<Beat> obtenerTodos() {
        return beatService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Beat obtenerPorId(@PathVariable Long id) {
        return beatService.obtenerPorId(id);
    }

    @PostMapping
    public Beat crear(@RequestBody Beat beat) {
        return beatService.guardar(beat);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        beatService.eliminar(id);
    }
}