package fi.vm.sade.ohjausparametrit.service.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.ohjausparametrit.service.model.Parameter;

@Repository
@Transactional(readOnly = true)
public interface ParamRepository extends
        PagingAndSortingRepository<Parameter, Long> {

    List<Parameter> findByPath(String path);
    List<Parameter> findByName(String path);
    Parameter findByPathAndName(String path, String name);
    
    
}
