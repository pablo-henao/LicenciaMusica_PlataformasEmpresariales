package music.license.repository;

import java.util.List;

import music.license.model.AcuerdoCreditosEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcuerdoCreditosEventoRepository extends JpaRepository<AcuerdoCreditosEvento, Long> {

    List<AcuerdoCreditosEvento> findByBeatIdOrderByFechaAsc(Long beatId);
}
