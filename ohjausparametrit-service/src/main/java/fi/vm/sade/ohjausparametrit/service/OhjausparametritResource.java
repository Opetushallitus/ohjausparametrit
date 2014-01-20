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

import java.util.Date;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.conversion.ConvertParameterToParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.dao.ParamRepository;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;

/**
 * 
 * @author mlyly
 * 
 *         Usage examples.
 * 
 *         <pre>
 * /.../parametri
 * /.../parametri/hello
 * 
 * /.../parametri/tarjonta
 * /.../parametri/tarjonta.hakukausi.alkaa/syksy
 * /.../parametri/organisaatiot
 * /.../parametri/organisaatiot/1.2.3.4.5
 * 
 * ie.
 * /.../parametri/category
 * /.../parametri/category/target
 * </pre>
 * 
 * @author mlyly
 */
@Path("/parametri")
@Transactional(readOnly = true)

public class OhjausparametritResource {

    private static final String NO_TARGET = "_no_target_";

    private Logger logger = LoggerFactory
            .getLogger(OhjausparametritResource.class);

    private ConvertParameterToParameterRDTO converter = new ConvertParameterToParameterRDTO();

    private final Map<Class<?>, Parameter.Type> typeMap;

    @Autowired
    private ParamRepository paramRepository;

    public OhjausparametritResource() {
        typeMap = ImmutableMap.<Class<?>, Parameter.Type> builder()
                .put(String.class, Parameter.Type.STRING)
                .put(Integer.class, Parameter.Type.INT)
                .put(Boolean.class, Parameter.Type.BOOLEAN).build();
    }

    @GET
    @Path("/hello")
    @Produces("text/plain")
    public String hello() {
        System.out.println("/hello");
        if (true) {
            for (Parameter p : paramRepository.findAll()) {
                logger.info("  p = {}", p);
            }
            logger.error("*** GENERATING DEMO DATA ***");

            createDemoParameter("security.maxConcurrentUsers", NO_TARGET, 500);
            // createDemoParameter("security.loginsAllowedBetween",
            // NO_TARGET, new Date(), new Date());
            // createDemoParameter("security.maxSessionTimeMs",
            // NO_TARGET, 2 * 60 * 60 * 1000);
            // createDemoParameter("security.maxInvalidLoginsAllowed",
            // NO_TARGET, 3);
            // createDemoParameter("security.loginsAllowed",
            // ParameterValue.NO_TARGET,
            // true);
            //
        }
        return "Well heeello! " + new Date();

    }

    private void createDemoParameter(String path, String target, Object value) {

        Parameter p = paramRepository.findByPathAndName(path, target);

        if (p == null) {
            p = new Parameter();

            p.setName(target);
            p.setPath(path);
            Parameter.Type type = typeMap.get(value.getClass());
            if (type == null) {
                throw new RuntimeException("Unknown parameter type:"
                        + value.getClass());
            }

            p.setType(type);

            p.setValue(value.toString());

            paramRepository.save(p);
        }
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    public Iterable<ParameterRDTO> list() {
        return Iterables.transform(paramRepository.findAll(), converter);
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    @Path("{path}")
    public Iterable<ParameterRDTO> listByPath(@PathParam("path") String path) {
        return Iterables.transform(paramRepository.findByPath(path), converter);
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    @Path("{path}/{name}")
    public ParameterRDTO getParameterByCategoryAndtarget(
            @PathParam("path") String path, @PathParam("name") String name) {
        return converter.apply(paramRepository.findByPathAndName(path, name));
    }

}
