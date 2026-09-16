package music.license.repository;

import music.license.model.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    Page<Compra> findByCompradorId(Long compradorId, Pageable pageable);
}
