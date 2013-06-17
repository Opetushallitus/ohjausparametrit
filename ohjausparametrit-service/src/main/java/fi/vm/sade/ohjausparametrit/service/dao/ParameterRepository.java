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
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author mlyly
 */
public interface ParameterRepository extends PagingAndSortingRepository<Parameter, String> {

    public Parameter findByPath(String path);

    // + path + target



//    @Query(value = "{ 'age' : { $lte : ?0 } }", fields = "{ 'lastname' : 1, 'age' : 1}")
//    public Page<User> findByAgeLTLight(int age, Pageable pageable);
//    public Collection<User> findByFirstname(String name);
//    @Query(value = "{ 'firstname' : ?0 }", fields = "{ 'lastname' : 1, 'age' : 1}")
//    public Collection<User> findByFirstnameLight(String firstname);
//    public Collection<User> findByLastname(String name);
//    public User findByUsername(String username);
//
}
