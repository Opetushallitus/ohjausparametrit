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
import fi.vm.sade.ohjausparametrit.service.dao.ParameterRepository;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValue;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueDate;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

        LOG.error("*** GENERATING DEMO DATA ***");

        {
            Parameter p = new Parameter();
            p.getDescription().put("kieli_fi", "Suomeksi");
            p.getDescription().put("kieli_sv", "Svensk");

            p.setName("Name");
            p.setPath("this.is." + new Random().nextInt(1000));
            p.setType(Parameter.Type.DATE);

            {
                // Spesific target value
                ParameterValueDate pv = new ParameterValueDate();
                pv.setTarget("1.2.3.4.1");
                pv.setValue(new Date(System.currentTimeMillis() - new Random().nextInt()));
                p.getValues().add(pv);
            }

            {
                // Generic common value
                ParameterValueDate pv = new ParameterValueDate();
                pv.setTarget(null);
                pv.setValue(new Date(System.currentTimeMillis() - new Random().nextInt()));
                p.getValues().add(pv);
            }

            parameterRepository.save(p);
        }

        {
            Parameter p = new Parameter();
            p.getDescription().put("kieli_fi", "Suomeksi");
            p.getDescription().put("kieli_sv", "Svensk");

            p.setName("Name");
            p.setPath("that.is." + new Random().nextInt(1000));
            p.setType(Parameter.Type.INTEGER);

            {
                ParameterValueInteger pv = new ParameterValueInteger();
                pv.setTarget("1.2.3.4.1");
                pv.setValue(new Random().nextInt());
                p.getValues().add(pv);
           }
            {
                ParameterValueInteger pv = new ParameterValueInteger();
                pv.setTarget(null);
                pv.setValue(new Random().nextInt());
                p.getValues().add(pv);
            }

            parameterRepository.save(p);
        }



        return "Well heeello! " + new Date();
    }


    // GET /
    @Override
    public List<ParameterRDTO> list() {
        LOG.debug("list()");

        List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();

        for (Parameter parameter : parameterRepository.findAll()) {
            result.add(conversionService.convert(parameter, ParameterRDTO.class));
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    // GET /haku, /tarjonta /stuff.that.is.under.me
    @Override
    public List<ParameterRDTO> listByCategory(String category) {
        LOG.debug("listByCategory('{}')", category);

        if (isEmpty(category)) {
            return list();
        }

        List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();

        for (Parameter parameter : parameterRepository.findByPathRegexp("^" + category)) {
            result.add(conversionService.convert(parameter, ParameterRDTO.class));
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    // GET /haku/1.2.3.4.1234
    @Override
    public List<ParameterRDTO> getParameterByCategoryAndtarget(String category, String target) {
        LOG.debug("getParameterByCategoryAndtarget('{}', '{}')", category, target);

        // The generic "no target / anything goes" category
        if (isEmpty(target) || "NONE".equalsIgnoreCase(target)) {
            target = null;
        }

        List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();

        for (Parameter parameter : parameterRepository.findByPathRegexp("^" + category)) {
            ParameterRDTO tmp = conversionService.convert(parameter, ParameterRDTO.class);
            result.add(tmp);

            // Remove other that desired values from parameter
            for (Iterator<ParameterValueRDTO> it = tmp.getValues().iterator(); it.hasNext();) {
                ParameterValueRDTO parameterValueRDTO = it.next();

                if (isTargetMatch(parameterValueRDTO.getTarget(), target)) {
                    // OK! Leave match to list
                } else {
                    // No match, remove from values collection.
                    it.remove();
                }
            }
        }

        LOG.debug("  --> {}", result);

        return result;
    }

    /**
     * Match given target to values target.
     *
     * @param valueTarget
     * @param requiredTarget
     * @return
     */
    private boolean isTargetMatch(String valueTarget, String requiredTarget) {
        return isEmpty(requiredTarget) ? isEmpty(valueTarget) : (!isEmpty(valueTarget) && valueTarget.equals(requiredTarget));
    }

    /**
     * Null or empty value.
     *
     * @param value
     * @return
     */
    private boolean isEmpty(String value) {
        return (value == null || value.isEmpty());
    }
}
