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

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.ohjausparametrit.service.model.Template;

@Repository
@Transactional(readOnly = true)
public interface TemplateRepository extends
        PagingAndSortingRepository<Template, Long> {

    /**
     * Hae koko pathilla.
     * @param path
     * @return
     */
    Template findByPath(String path);
    
    /**
     * Hae path prefixillä.
     * @param path
     * @return
     */
    Iterable<Template> findByPathStartingWith(String path);

}
