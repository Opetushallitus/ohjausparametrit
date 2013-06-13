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
import fi.vm.sade.ohjausparametrit.api.model.ParameterValueRDTO;
import fi.vm.sade.ohjausparametrit.service.dao.ParameterDAO;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private ParameterDAO parameterDAO;

    @Override
    public String hello() {
        LOG.debug("hello()");

        for (Parameter p : parameterDAO.findAllParameters()) {
            LOG.info("  p = {}", p);
        }

        for (ParameterValue pv : parameterDAO.findAllParameterValues()) {
            LOG.info("  pv = {}", pv);
        }

        return "Well heeello! " + new Date();
    }

    @Override
    public List<ParameterRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.debug("search({}, {}, {}, {}, {}", new Object[] {searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();

        for (Parameter parameter : parameterDAO.findAllParameters()) {
            result.add(conversionService.convert(parameter, ParameterRDTO.class));
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    @Override
    public ParameterRDTO findByPath(String path) {
        LOG.warn("findByPath({})", path);

        ParameterRDTO result = conversionService.convert(parameterDAO.findParemeterByPath(path), ParameterRDTO.class);
        if (result != null) {
            result.setValues(convert(parameterDAO.findParameterValuesByPath(path)));
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    @Override
    public ParameterRDTO findByPathAndTarget(String path, String target) {
        LOG.warn("findByPathAndTarget({}, {})", path, target);

        ParameterRDTO result = conversionService.convert(parameterDAO.findParemeterByPath(path), ParameterRDTO.class);
        if (result != null) {
            List<ParameterValueRDTO> values = null;

            // Add the spesific parameter value
            ParameterValue pv = parameterDAO.findParameterValueByPathAndTarget(path, target);
            if (pv != null) {
                values = new ArrayList<ParameterValueRDTO>();
                values.add(conversionService.convert((ParameterValue) pv, ParameterValueRDTO.class));
            }

            result.setValues(values);
        }


        LOG.debug("  --> {}", result);

        return result;
    }

    private List<ParameterValueRDTO> convert(List<ParameterValue> findParameterValuesByPath) {
        List<ParameterValueRDTO> result = null;

        if (findParameterValuesByPath != null && !findParameterValuesByPath.isEmpty()) {
            result = new ArrayList<ParameterValueRDTO>();

            for (ParameterValue parameterValue : findParameterValuesByPath) {
                result.add(conversionService.convert((ParameterValue) parameterValue, ParameterValueRDTO.class));
            }
        }

        return result;
    }




}
