package music.license.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import music.license.dto.beat.BeatRequest;
import music.license.dto.beat.BeatResponse;
import music.license.model.Beat;
import music.license.model.EstadoBeat;
import music.license.model.Usuario;
import music.license.service.BeatService;

@RestController
@RequestMapping("/api/beats")
public class BeatController {

    private final BeatService beatService;

    public BeatController(BeatService beatService) {
        this.beatService = beatService;
    }

    @GetMapping
    public List<BeatResponse> obtenerTodos() {
        return beatService.obtenerTodos().stream()
                .map(BeatResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public BeatResponse obtenerPorId(@PathVariable Long id) {
        return BeatResponse.desde(beatService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeatResponse crear(@Valid @RequestBody BeatRequest request, @AuthenticationPrincipal Usuario usuario) {
        Beat beat = new Beat();
        beat.setTitulo(request.getTitulo());
        beat.setGenero(request.getGenero());
        beat.setBpm(request.getBpm());
        beat.setUrlPreview(request.getUrlPreview());
        beat.setProductor(usuario);
        beat.setEstado(EstadoBeat.BORRADOR);

        return BeatResponse.desde(beatService.guardar(beat));
    }

    @PutMapping("/{id}")
    public BeatResponse actualizar(@PathVariable Long id, @Valid @RequestBody BeatRequest request) {
        return BeatResponse.desde(beatService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        beatService.eliminar(id);
    }
}
