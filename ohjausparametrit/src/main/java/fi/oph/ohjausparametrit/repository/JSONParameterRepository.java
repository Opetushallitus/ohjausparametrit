package fi.oph.ohjausparametrit.repository;

import fi.oph.ohjausparametrit.model.JSONParameter;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface JSONParameterRepository extends PagingAndSortingRepository<JSONParameter, String> {

  JSONParameter findByTarget(String target);

  List<JSONParameter> findByTargetIn(List<String> target);

  @Query(value = "SELECT p FROM JSONParameter p WHERE p.muokattu BETWEEN ?1 AND ?2")
  List<JSONParameter> findByTimeRange(
      Date startDatetime, Date endDatetime, PageRequest pageRequest);

  @Query(value = "SELECT p FROM JSONParameter p WHERE p.muokattu < ?1")
  List<JSONParameter> findByEndTime(Date endDatetime, PageRequest pageRequest);
}
