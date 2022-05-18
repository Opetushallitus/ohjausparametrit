package fi.oph.ohjausparametrit.repository;

import fi.oph.ohjausparametrit.model.JSONParameter;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface JSONParameterRepository extends PagingAndSortingRepository<JSONParameter, String> {

  JSONParameter findByTarget(String target);

  List<JSONParameter> findByTargetIn(List<String> target);
}
