package music.license.repository;

import java.util.List;

import music.license.model.ColaboradorBeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorBeatRepository extends JpaRepository<ColaboradorBeat, Long> {

    List<ColaboradorBeat> findByBeatId(Long beatId);
}