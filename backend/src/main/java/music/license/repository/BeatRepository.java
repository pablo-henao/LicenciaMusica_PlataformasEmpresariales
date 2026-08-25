package music.license.repository;

import music.license.model.Beat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeatRepository extends JpaRepository<Beat, Long> {

}