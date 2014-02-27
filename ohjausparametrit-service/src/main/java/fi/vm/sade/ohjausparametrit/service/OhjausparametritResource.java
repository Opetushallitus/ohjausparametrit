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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.conversion.ParameterRDTOToParameter;
import fi.vm.sade.ohjausparametrit.service.conversion.ParameterToParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.conversion.TemplateToTemplateRDTO;
import fi.vm.sade.ohjausparametrit.service.dao.ParamRepository;
import fi.vm.sade.ohjausparametrit.service.dao.TemplateRepository;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.Template;
import fi.vm.sade.ohjausparametrit.service.model.Template.Type;

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
@Transactional
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
                .put(Integer.class, Template.Type.LONG)
                .put(Long.class, Template.Type.LONG)
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

            saveDateTemplate("PH_TJT", Type.DATE);
            saveDateTemplate("PH_HKLPT", Type.DATE);
            saveDateTemplate("PH_HKMT", Type.DATE);
            saveDateTemplate("PH_KKM_S", Type.DATE);
            saveDateTemplate("PH_KKM_E", Type.DATE);
            saveDateTemplate("PH_HVVPTP", Type.DATE);
            saveDateTemplate("PH_KTT_S", Type.DATE);
            saveDateTemplate("PH_KTT_E", Type.DATE);
            saveDateTemplate("PH_OLVVPKE_S", Type.DATE);
            saveDateTemplate("PH_OLVVPKE_E", Type.DATE);
            saveDateTemplate("PH_VLS_S", Type.DATE);
            saveDateTemplate("PH_VLS_E", Type.DATE);
            saveDateTemplate("PH_SS_S", Type.DATE);
            saveDateTemplate("PH_SS_E", Type.DATE);
            saveDateTemplate("PH_SSAVTM", Type.DATE);
            saveDateTemplate("PH_SST", Type.LONG);
            saveDateTemplate("PH_SSKA", Type.DATE);
            saveDateTemplate("PH_VTSSV", Type.DATE);
            saveDateTemplate("PH_VSSAV", Type.DATE);
            saveDateTemplate("PH_JKLIP", Type.DATE);
            saveDateTemplate("PH_HKP", Type.DATE);
            saveDateTemplate("PH_VTJH_S", Type.DATE);
            saveDateTemplate("PH_VTJH_E", Type.DATE);
            saveDateTemplate("PH_EVR", Type.DATE);
            saveDateTemplate("PH_OPVP", Type.DATE);
            saveDateTemplate("PH_HPVOA", Type.LONG);
            saveDateTemplate("PH_HKTA", Type.DATE);
            
            //Hakukausi
            saveDateTemplate("PHK_HKAR_S", Type.DATE);
            saveDateTemplate("PHK_HKAR_E", Type.DATE);
            saveDateTemplate("PHK_PLPS_S", Type.DATE);
            saveDateTemplate("PHK_PLPS_E", Type.DATE);
            saveDateTemplate("PHK_PLAS_S",Type.DATE);
            saveDateTemplate("PHK_PLAS_E", Type.DATE);
            saveDateTemplate("PHK_LPAS_S", Type.DATE);
            saveDateTemplate("PHK_LPAS_E", Type.DATE);
            
            //KELA
            saveDateTemplate("PHK_KELAKTTS", Type.DATE);
            saveDateTemplate("PHK_KELATAVS_S",Type.DATE);
            saveDateTemplate("PHK_KELATAVS_E",Type.DATE);
            saveDateTemplate("PHK_KELATAVSM",Type.BOOLEAN);
            saveDateTemplate("PHK_KELAKAVTS_S",Type.DATE);
            saveDateTemplate("PHK_KELAKAVTS_E",Type.DATE);
            saveDateTemplate("PHK_KELAKAVTSM",Type.BOOLEAN);
            saveDateTemplate("PHK_KELAVTST",Type.LONG);
            saveDateTemplate("PHK_KELAVTSAK",Type.STRING);

            // TEM
            saveDateTemplate("PHK_TEMTAVS_S",Type.DATE);
            saveDateTemplate("PHK_TEMATAVS_E",Type.DATE);
            saveDateTemplate("PHK_TEMTAVSM",Type.BOOLEAN);
            saveDateTemplate("PHK_TEMKAVTS_S",Type.DATE);
            saveDateTemplate("PHK_TEMKAVTS_E",Type.DATE);
            saveDateTemplate("PHK_TEMKAVTSM",Type.BOOLEAN);
            saveDateTemplate("PHK_TEMVTST",Type.LONG);
            saveDateTemplate("PHK_TEMVTSAK",Type.STRING);

        }
        return "Well heeello! " + new Date();
    }

    private void saveDateTemplate(String path, Type type) {
        Template t = templateRepository.findByPath(path);
        if (t == null) {
            System.out.println("creating new template for path" + path);
            t = new Template();
        }
        t.setType(type);
        t.setRequired(false);
        t.setPath(path);
        templateRepository.save(t);
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
    @Path("template")
    public Response listAllTemplates() {
        return Response.ok(Iterables.transform(templateRepository.findAll(), templateConverter)).build();
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    @Path("template/{path}")
    public Response getTemplatesByPathPrefix(@PathParam("path") String path) {
        logger.info("finding templates with prefix:" + path);
        return Response.ok(Iterables.transform(templateRepository.findByPathStartingWith(path), templateConverter)).build();
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
        return Response.ok(Lists.newArrayList(Iterables.transform(paramRepository.findByPathStartingWithAndName(path, name), paramConverter))).build();
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

    @PUT
    @Produces("application/json;charset=UTF-8")
    @Path("{path}/{name}")
    public Response setParameter(@PathParam("path") String path, @PathParam("name") String name, Param value) {

        if(value.value==null) {
            logger.info("deleting parameter: path:" + path + " name:" + name);
            //remove param
            deleteParameter(path,  name);
            return Response.ok().build();
        }
        

        Parameter p = paramRepository.findByPathAndName(path, name);
        if(p==null) {
            logger.info("creating new parameter: path:" + path + " name:" + name);
            p= new Parameter();
            p.setPath(path);
            p.setName(name);
        }

        Template t = templateRepository.findByPath(path);
        if (t == null) {
            t = createTemplate(path, name, value);
        } else {
            logger.info("validating...");
            //simple validations for data
            switch(t.getType()) {
            case BOOLEAN:
                if(value.value.getClass()!=Boolean.class) {
                    return error("not a boolean: '" + value.value + "'");
                }
                break;
            case DATE:
                try{
                    Long.parseLong(value.value.toString());
                } catch (NumberFormatException nfe) {
                    return error("not a date: '" + value.value + "'");
                }
                break;
            case LONG:
                try{
                    Long.parseLong(value.value.toString());
                } catch (NumberFormatException nfe) {
                    return error("not a long: '" + value.value + "'");
                }
                break;
            case STRING:
                if(value.value.getClass()!=String.class) {
                    return error("not a string: '" + value.value + "'");
                }
                break;
            default:
                break;
            
            }
        }

        
        p.setValue(value.value);
        paramRepository.save(p);
        return Response.ok().build();
    }

    private Response error(String message) {
        logger.warn(message);
        return Response.status(Status.BAD_REQUEST).entity(message).build();
    }

    private Template createTemplate(String path, String name, Param value) {
        logger.info("creating new template");

        Template t = templateRepository.findByPath(path);
        if(t!=null) {
            return t;
        }

        System.out.println("Creating new template for path" + path);
        t = new Template();
        
        t.setType(typeMap.get(value.value.getClass()));
        t.setRequired(false);
        t.setPath(path);
        if(t.getType()==null) {
            throw new RuntimeException("Could not quess template type from value:" + value + " please create template first");
        }
        
        return templateRepository.save(t);
    }

}
