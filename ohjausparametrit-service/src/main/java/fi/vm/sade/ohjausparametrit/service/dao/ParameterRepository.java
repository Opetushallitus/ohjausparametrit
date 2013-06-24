/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.ohjausparametrit.service.dao;

import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import java.util.Collection;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Define the finders for Parameter repo.
 *
 * http://static.springsource.org/spring-data/mongodb/docs/1.2.x/reference/html/mongo.repositories.html
 *
 * @author mlyly
 */
public interface ParameterRepository extends PagingAndSortingRepository<Parameter, String> {

    /**
     * Find Parameter by exact path.
     *
     * @param path
     * @return
     */
    // @Query(value = "{ 'path' : ?0 }")
    public Parameter findByPath(String path);

    /**
     * Find by path regexp.
     *
     * @param path regexp
     * @return
     */
    @Query(value = "{ 'path' : { '$regex' : ?0 } }")
    public Collection<Parameter> findByPathRegexp(String path);

//    @Query(value = "{ 'age' : { $lte : ?0 } }", fields = "{ 'lastname' : 1, 'age' : 1}")
//    @Query(value = "{ 'firstname' : ?0 }", fields = "{ 'lastname' : 1, 'age' : 1}")
}
