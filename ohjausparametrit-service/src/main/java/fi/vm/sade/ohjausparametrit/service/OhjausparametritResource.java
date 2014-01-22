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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.conversion.ParameterRDTOToParameter;
import fi.vm.sade.ohjausparametrit.service.conversion.ParameterToParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.conversion.TemplateToTemplateRDTO;
import fi.vm.sade.ohjausparametrit.service.dao.ParamRepository;
import fi.vm.sade.ohjausparametrit.service.dao.TemplateRepository;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.Template;

/**
 * TODO add swagger, annotate api
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

    private ParameterToParameterRDTO paramConverter = new ParameterToParameterRDTO();
    private TemplateToTemplateRDTO templateConverter = new TemplateToTemplateRDTO();
    private ParameterRDTOToParameter parameterRdtoConverter = new ParameterRDTOToParameter();

    private final Map<Class<?>, Template.Type> typeMap;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private TemplateRepository templateRepository;

    public OhjausparametritResource() {
        typeMap = ImmutableMap.<Class<?>, Template.Type> builder()
                .put(String.class, Template.Type.STRING)
                .put(Integer.class, Template.Type.INT)
                .put(Boolean.class, Template.Type.BOOLEAN)
                .put(Date.class, Template.Type.DATE).build();
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

            createDemoParameter("securityMaxConcurrentUsers", NO_TARGET, 500,
                    true);
            createDemoParameter("alkupvm", NO_TARGET, new Date(), true);
            createDemoParameter("securityLoginsAllowed", NO_TARGET, true, true);
            createDemoParameter("hello", NO_TARGET, "world!", true);
        }
        return "Well heeello! " + new Date();
    }

    private Response createDemoParameter(String path, String target,
            Object value, boolean required) {

        Parameter p = paramRepository.findByPathAndName(path, target);

        if (p == null) {
            Template t = templateRepository.findByPath(path);
            if (t == null) {
                System.out.println("creating new template for path" + path);
                t = new Template();
                t.setRequired(required);
                t.setPath(path);
            }

            Template.Type type = typeMap.get(value.getClass());
            if (type == null) {
                throw new RuntimeException("Unknown parameter type:"
                        + value.getClass());
            }

            t.setType(type);

            templateRepository.save(t);

            p = new Parameter();
        }

        p.setName(target);
        p.setPath(path);

        p.setValue(value);

        paramRepository.save(p);

        return Response.ok().build();
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    public Iterable<ParameterRDTO> list() {
        logger.info("list all");
        return Iterables.transform(paramRepository.findAll(), paramConverter);
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    @Path("template/{path}")
    public Response getTemplateByPath(@PathParam("path") String path) {
        try {
            logger.info("list all");
            Template t = templateRepository.findByPath(path);
            if (t == null) {
                logger.debug("not found:" + path);
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(templateConverter.apply(t)).build();

            }
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    /**
     * Hakee parametrin 
     * 
     * @param path path prefix (esim HK)
     * @param name name(esim oid)
     * @return
     */
    @GET
    @Produces("application/json;charset=UTF-8")
    @Path("{path}/{name}")
    public Response getParameterByPathAndName(
            @PathParam("path") String path, @PathParam("name") String name) {
        return Response.ok(Iterables.transform(paramRepository.findByPathStartingWithAndName(path, name), paramConverter)).build();
    }

    /**
     * Listaa parametrit
     * 
     * @param path
     *            polku (prefix)
     * @return
     */
    @GET
    @Produces("application/json;charset=UTF-8")
    @Path("{path}")
    public Iterable<ParameterRDTO> listByPath(@PathParam("path") String path) {
        logger.info("list path starting with:" + path);
        return Iterables.transform(
                paramRepository.findByPathStartingWith(path), paramConverter);
    }

    /**
     * Poista parametri.
     * 
     * @param path
     * @param name
     * @return
     */
    @DELETE
    @Produces("application/json;charset=UTF-8")
    @Path("{path}/{name}")
    public Response deleteParameter(@PathParam("path") String path,
            @PathParam("name") String name) {
        Parameter p = paramRepository.findByPathAndName(path, name);
        if (p == null) {
            logger.debug("not found:" + path + "," + name);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        paramRepository.delete(p);
        return Response.ok(p).build();
    }

    @POST
    @Produces("application/json;charset=UTF-8")
    public Response createParameter(ParameterRDTO p) {
        paramRepository.save(parameterRdtoConverter.apply(p));
        return Response.ok().build();
    }

    @PUT
    @Produces("application/json;charset=UTF-8")
    @Path("{path}")
    public Response updateParameter(@PathParam("path") String path, Parameter p) {

        paramRepository.save(p);
        return Response.ok().build();
    }

}
