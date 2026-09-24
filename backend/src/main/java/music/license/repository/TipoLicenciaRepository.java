package music.license.repository;

import music.license.model.TipoLicencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoLicenciaRepository extends JpaRepository<TipoLicencia, Long> {

    List<TipoLicencia> findByBeatId(Long beatId);

}