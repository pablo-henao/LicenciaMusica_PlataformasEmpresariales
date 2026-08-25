package music.license.repository;

import music.license.model.TipoLicencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoLicenciaRepository extends JpaRepository<TipoLicencia, Long> {

}