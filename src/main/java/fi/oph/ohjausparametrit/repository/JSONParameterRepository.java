package fi.oph.ohjausparametrit.repository;

import fi.oph.ohjausparametrit.model.JSONParameter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface JSONParameterRepository extends JpaRepository<JSONParameter, String> {

  JSONParameter findByTarget(String target);

  List<JSONParameter> findByTargetIn(List<String> target);
}
