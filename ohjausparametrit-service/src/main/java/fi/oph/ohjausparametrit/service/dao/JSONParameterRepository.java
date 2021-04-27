package fi.oph.ohjausparametrit.service.dao;

import fi.oph.ohjausparametrit.service.model.JSONParameter;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple repo for json parameters.
 *
 * @author mlyly
 */
@Repository
@Transactional(readOnly = true)
public interface JSONParameterRepository extends
        PagingAndSortingRepository<JSONParameter, String> {

    /**
     * Find by "primary key".
     * 
     * @param target
     * @return 
     */
    public JSONParameter findByTarget(String target);

}
