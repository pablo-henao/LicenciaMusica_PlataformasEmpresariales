package music.license.service;

import java.util.List;

import org.springframework.stereotype.Service;

import music.license.model.Compra;
import music.license.repository.CompraRepository;

@Service
public class CompraService {

    private final CompraRepository compraRepository;

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public List<Compra> obtenerTodas() {
        return compraRepository.findAll();
    }

    public Compra obtenerPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }

    public Compra guardar(Compra compra) {
        return compraRepository.save(compra);
    }

    public void eliminar(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new RuntimeException("Compra no encontrada");
        }

        compraRepository.deleteById(id);
    }
}