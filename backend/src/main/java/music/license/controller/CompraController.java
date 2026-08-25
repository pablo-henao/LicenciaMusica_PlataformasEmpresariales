package music.license.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import music.license.model.Compra;
import music.license.service.CompraService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> obtenerTodas() {
        return compraService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Compra obtenerPorId(@PathVariable Long id) {
        return compraService.obtenerPorId(id);
    }

    @PostMapping
    public Compra crear(@RequestBody Compra compra) {
        return compraService.guardar(compra);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        compraService.eliminar(id);
    }
}