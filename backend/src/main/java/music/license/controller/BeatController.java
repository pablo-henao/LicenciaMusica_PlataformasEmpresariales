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
    @ResponseStatus(HttpStatus.CREATED)
    public Beat crear(@RequestBody Beat beat) {
        return beatService.guardar(beat);
    }

    @PutMapping("/{id}")
    public Beat actualizar(@PathVariable Long id, @RequestBody Beat beat) {
        return beatService.actualizar(id, beat);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        beatService.eliminar(id);
    }
}
