package music.license.repository;

import java.util.List;

import music.license.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByCompradorId(Long compradorId);
}