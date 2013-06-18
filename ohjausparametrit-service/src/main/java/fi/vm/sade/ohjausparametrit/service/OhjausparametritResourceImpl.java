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
package fi.vm.sade.ohjausparametrit.service;

import fi.vm.sade.ohjausparametrit.api.OhjausparametritResource;
import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.dao.ParameterRepository;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlyly
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class OhjausparametritResourceImpl implements OhjausparametritResource {

    private static final Logger LOG = LoggerFactory.getLogger(OhjausparametritResourceImpl.class);
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ParameterRepository parameterRepository;

    @Override
    public String hello() {
        LOG.debug("hello()");

        for (Parameter p : parameterRepository.findAll()) {
            LOG.info("  p = {}", p);
        }

        Parameter p = new Parameter();
        p.getDescription().put("kieli_fi", "Suomeksi");
        p.getDescription().put("kieli_sv", "Svensk");

        p.setName("Name");
        p.setPath("this.is." + new Random().nextInt(1000));

        parameterRepository.save(p);

        return "Well heeello! " + new Date();
    }

    @Override
    public List<ParameterRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.debug("search({}, {}, {}, {}, {}", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();

        // TODO use pages!
        for (Parameter parameter : parameterRepository.findAll()) {
            result.add(conversionService.convert(parameter, ParameterRDTO.class));
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    @Override
    public ParameterRDTO findByPath(String path) {
        LOG.warn("findByPath({})", path);

        ParameterRDTO result = null;

        Parameter p = parameterRepository.findByPath(path);
        if (p != null) {
            result = conversionService.convert(p, ParameterRDTO.class);
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    @Override
    public ParameterRDTO findByPathAndTarget(String path, String target) {
        LOG.warn("findByPathAndTarget({}, {})", path, target);

        return findByPath(path);
    }
}
