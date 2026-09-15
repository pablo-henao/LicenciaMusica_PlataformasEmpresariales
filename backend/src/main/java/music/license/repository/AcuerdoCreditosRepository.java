package music.license.repository;

import java.util.Optional;

import music.license.model.AcuerdoCreditos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcuerdoCreditosRepository extends JpaRepository<AcuerdoCreditos, Long> {

    Optional<AcuerdoCreditos> findByBeatId(Long beatId);
}