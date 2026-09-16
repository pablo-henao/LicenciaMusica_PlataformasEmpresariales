package music.license.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import music.license.model.Beat;
import music.license.model.EstadoBeat;

public interface BeatRepository extends JpaRepository<Beat, Long> {

    @Query("""
            SELECT b FROM Beat b
            WHERE b.estado = :estado
            AND (:genero IS NULL OR LOWER(b.genero) = LOWER(:genero))
            AND (:bpmMin IS NULL OR b.bpm >= :bpmMin)
            AND (:bpmMax IS NULL OR b.bpm <= :bpmMax)
            AND (:titulo IS NULL OR LOWER(b.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
            """)
    Page<Beat> buscarCatalogo(
            @Param("estado") EstadoBeat estado,
            @Param("genero") String genero,
            @Param("bpmMin") Integer bpmMin,
            @Param("bpmMax") Integer bpmMax,
            @Param("titulo") String titulo,
            Pageable pageable);

    Page<Beat> findByProductorId(Long productorId, Pageable pageable);
}
