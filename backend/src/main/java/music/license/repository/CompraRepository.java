package music.license.repository;

import music.license.model.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    Page<Compra> findByCompradorId(Long compradorId, Pageable pageable);

    @Query("SELECT c FROM Compra c WHERE c.tipoLicencia.beat.productor.id = :productorId")
    Page<Compra> findByProductorId(@Param("productorId") Long productorId, Pageable pageable);
}
