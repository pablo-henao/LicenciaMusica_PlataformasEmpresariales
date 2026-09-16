package music.license.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import jakarta.validation.Valid;

import music.license.dto.acuerdo.AcuerdoCreditosEventoResponse;
import music.license.dto.beat.BeatRequest;
import music.license.dto.beat.BeatResponse;
import music.license.dto.common.PaginaResponse;
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

    /**
     * Catalogo publico: solo beats publicados, filtrable por genero/bpm y paginado.
     */
    @GetMapping
    public PaginaResponse<BeatResponse> obtenerCatalogo(
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer bpmMin,
            @RequestParam(required = false) Integer bpmMax,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Beat> pagina = beatService.buscarCatalogo(genero, bpmMin, bpmMax, pageable);
        return PaginaResponse.desde(pagina, BeatResponse::desde);
    }

    /**
     * Los beats del productor autenticado, incluidos los que siguen en borrador.
     */
    @GetMapping("/mios")
    public PaginaResponse<BeatResponse> obtenerMios(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Beat> pagina = beatService.obtenerMios(usuario, pageable);
        return PaginaResponse.desde(pagina, BeatResponse::desde);
    }

    @GetMapping("/{id}")
    public BeatResponse obtenerPorId(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return BeatResponse.desde(beatService.obtenerPorIdPublico(id, usuario));
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

        return BeatResponse.desde(beatService.guardar(beat, usuario));
    }

    @PutMapping("/{id}")
    public BeatResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody BeatRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        return BeatResponse.desde(beatService.actualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/publicar")
    public BeatResponse publicar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return BeatResponse.desde(beatService.publicar(id, usuario));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        beatService.eliminar(id, usuario);
    }

    /**
     * Historial de quien propuso, acepto, rechazo o modifico el acuerdo de creditos del beat.
     * Visible solo para el productor dueño y los colaboradores invitados.
     */
    @GetMapping("/{id}/historial-creditos")
    public List<AcuerdoCreditosEventoResponse> obtenerHistorialCreditos(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {

        return beatService.obtenerHistorialCreditos(id, usuario).stream()
                .map(AcuerdoCreditosEventoResponse::desde)
                .toList();
    }
}
