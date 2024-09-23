package fi.oph.ohjausparametrit.ovara.ajastus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SiirtotiedostoProsessiRepository
    extends CrudRepository<SiirtotiedostoProsessi, Integer> {

  @Query(
      value =
          "SELECT execution_uuid, window_start, window_end, run_start, run_end, info, success, error_message from siirtotiedosto where success order by run_end desc limit 1",
      nativeQuery = true)
  SiirtotiedostoProsessi findLatestSuccessful();
}
