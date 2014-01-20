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
 * /.../parametri/tarjonta/1.2.3.4.5
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
public class OhjausparametritResourceImpl {

    private static final String NO_TARGET = "_no_target_";

    private Logger logger = LoggerFactory
            .getLogger(OhjausparametritResourceImpl.class);

    private ConvertParameterToParameterRDTO converter = new ConvertParameterToParameterRDTO();

    private final Map<Class<?>, Parameter.Type> typeMap;

    @Autowired
    private ParamRepository paramRepository;

    public OhjausparametritResourceImpl() {
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

    // private static final Logger LOG =
    // LoggerFactory.getLogger(OhjausparametritResourceImpl.class);
    // @Autowired
    // private ConversionService conversionService;
    // @Autowired
    // private ParamRepository parameterRepository;
    //
    // @Override
    // public String hello() {
    // LOG.debug("hello()");
    //
    // if (true) {
    // for (Param p : parameterRepository.findAll()) {
    // LOG.info("  p = {}", p);
    // }
    // LOG.error("*** GENERATING DEMO DATA ***");
    //
    // // createDemoParameter("tarjonta.julkaisunTakaraja",
    // ParameterValue.NO_TARGET, new Date(), null);
    // // createDemoParameter("tarjonta.aloituspaikkojenMuokkauksenTakaraja",
    // ParameterValue.NO_TARGET, new Date(), null);
    //
    // for (String hakukausi : new String[]{"hakukausi_kevat_2014",
    // "hakukausi_syksy_2014"}) {
    // // createDemoParameter("hakukausi.aikaraja", hakukausi, new Date(), new
    // Date());
    // //
    // // createDemoParameter("hakukausi.perustiedot.siirto", hakukausi, new
    // Date(), new Date());
    // // createDemoParameter("hakukausi.arvosanat.siirto", hakukausi, new
    // Date(), new Date());
    // // createDemoParameter("hakukausi.lukionpaattoarvosanat.siirto",
    // hakukausi, new Date(), new Date());
    //
    // // createDemoParameter("hakukausi.kela.tarjonta.siirto", hakukausi, new
    // Date(), null);
    // // createDemoParameter("hakukausi.kela.valinnat.toinenaste.siirto",
    // hakukausi, new Date(), new Date());
    // // createDemoParameter("hakukausi.kela.valinnat.korkeakouluaste.siirto",
    // hakukausi, new Date(), new Date());
    //
    // createDemoParameter("hakukausi.kela.tiedonsiirtoAinaValintojenMuuttuessa",
    // hakukausi, true);
    // createDemoParameter("hakukausi.kela.tiedonsiirtoTiheys", hakukausi, 7);
    // createDemoParameter("hakukausi.kela.tiedonsiirtoTiheys.kellonaika",
    // hakukausi, "00:00:00");
    //
    // // createDemoParameter("hakukausi.tem.valinnat.toinenaste.siirto",
    // hakukausi, new Date(), null);
    // // createDemoParameter("hakukausi.tem.valinnat.korkeakouluaste.siirto",
    // hakukausi, new Date(), null);
    // createDemoParameter("hakukausi.tem.tiedonsiirtoAinaValintojenMuuttuessa",
    // hakukausi, true);
    // createDemoParameter("hakukausi.tem.tiedonsiirtoTiheys", hakukausi, 7);
    // createDemoParameter("hakukausi.tem.tiedonsiirtoTiheys.kellonaika",
    // hakukausi, "00:00:00");
    // }
    //
    // for (String hakuOid : new String[]{"haku_1.2.3.4.5", "haku_1.2.3.4.55"})
    // {
    // // createDemoParameter("tarjonta.julkaisunTakaraja", hakuOid, new Date(),
    // null);
    // // createDemoParameter("tarjonta.aloituspaikkojenMuokkauksenTakaraja",
    // hakuOid, new Date(), null);
    //
    // // createDemoParameter("valinnat.koekutsujenMuodostaminen", hakuOid, new
    // Date(), new Date());
    // //
    // createDemoParameter("valinnat.harkinnanvaraisetPaatoksetTallennusTakaraja",
    // hakuOid, new Date(), null);
    // // createDemoParameter("valinnat.koetulostenTallentaminen", hakuOid, new
    // Date(), new Date());
    // //
    // createDemoParameter("valinnat.oppilaitostenVirkailijoidenValitapalveluEstetty",
    // hakuOid, new Date(), new Date());
    // // createDemoParameter("valinnat.valintalaskennanSuorittaminen", hakuOid,
    // new Date(), new Date());
    // // createDemoParameter("valinnat.sijoittelunSuorittaminen", hakuOid, new
    // Date(), new Date());
    //
    // createDemoParameter("valinnat.suoritetaanSijoitteluAinaValintatietojenMuuttuessa",
    // hakuOid, false);
    // createDemoParameter("valinnat.sijoittelunSuorittamistiheys", hakuOid,
    // 48);
    // createDemoParameter("valinnat.sijoittelunSuorittamisaika", hakuOid,
    // "00:00:00");
    //
    // // createDemoParameter("valinnat.jalkiohjauskirjeidenLahetys", hakuOid,
    // new Date(), null);
    // // createDemoParameter("valinnat.hakukierrosPaattyy", hakuOid, new
    // Date(), null);
    // }
    //
    // createDemoParameter("security.maxConcurrentUsers",
    // ParameterValue.NO_TARGET, 500);
    // // createDemoParameter("security.loginsAllowedBetween",
    // ParameterValue.NO_TARGET, new Date(), new Date());
    // createDemoParameter("security.maxSessionTimeMs",
    // ParameterValue.NO_TARGET, 2 * 60 * 60 * 1000);
    // createDemoParameter("security.maxInvalidLoginsAllowed",
    // ParameterValue.NO_TARGET, 3);
    // createDemoParameter("security.loginsAllowed", ParameterValue.NO_TARGET,
    // true);
    //
    // return "CREATED DEMO DATA!  " + new Date();
    // }
    //
    // return "Well heeello! " + new Date();
    // }
    //
    // // GET /
    // @Override
    // public List<ParameterRDTO> list() {
    // LOG.debug("list()");
    //
    // List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();
    //
    // for (Param parameter : parameterRepository.findAll()) {
    // result.add(conversionService.convert(parameter, ParameterRDTO.class));
    // }
    //
    // LOG.debug("  --> {}", result);
    //
    // return result;
    // }
    //
    // // GET /haku, /tarjonta /stuff.that.is.under.me
    // @Override
    // public List<ParameterRDTO> listByCategory(String category) {
    // LOG.debug("listByCategory('{}')", category);
    //
    // if (isEmpty(category)) {
    // return list();
    // }
    //
    // List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();
    //
    // // for (Parameter parameter : parameterRepository.f.findByPathRegexp("^"
    // + category)) {
    // // result.add(conversionService.convert(parameter, ParameterRDTO.class));
    // // }
    //
    // LOG.debug("  --> {}", result);
    //
    // return result;
    // }
    //
    // // GET /haku/1.2.3.4.1234
    // @Override
    // public List<ParameterRDTO> getParameterByCategoryAndtarget(String
    // category, String target) {
    // LOG.debug("getParameterByCategoryAndtarget('{}', '{}')", category,
    // target);
    //
    // // The generic "no target / anything goes" category
    // if (isEmpty(target)) {
    // target = ParameterValue.NO_TARGET;
    // }
    //
    // List<ParameterRDTO> result = new ArrayList<ParameterRDTO>();
    //
    // // for (Parameter parameter : parameterRepository.findByPathRegexp("^" +
    // category)) {
    // // ParameterRDTO tmp = conversionService.convert(parameter,
    // ParameterRDTO.class);
    // // result.add(tmp);
    // //
    // // // Remove other that desired values from parameter
    // // for (Iterator<ParameterValueRDTO> it = tmp.getValues().iterator();
    // it.hasNext();) {
    // // ParameterValueRDTO parameterValueRDTO = it.next();
    // //
    // // if (isTargetMatch(parameterValueRDTO.getTarget(), target)) {
    // // // OK! Leave match to list
    // // } else {
    // // // No match, remove from values collection.
    // // it.remove();
    // // }
    // // }
    // // }
    //
    // LOG.debug("  --> {}", result);
    //
    // return result;
    // }
    //
    // /**
    // * Match given target to values target.
    // *
    // * @param valueTarget
    // * @param requiredTarget
    // * @return
    // */
    // private boolean isTargetMatch(String valueTarget, String requiredTarget)
    // {
    // return isEmpty(requiredTarget) ? isEmpty(valueTarget) :
    // (!isEmpty(valueTarget) && valueTarget.equals(requiredTarget));
    // }
    //
    // /**
    // * Null or empty value.
    // *
    // * @param value
    // * @return
    // */
    // private boolean isEmpty(String value) {
    // return (value == null || value.isEmpty());
    // }
    //
    //
    //
    // private void createDemoParameter(String path, String target, String
    // value) {
    //
    // Param p = parameterRepository.findByPath(path);
    //
    // if (p == null) {
    // p = new Param();
    // // p.getDescription().put("kieli_fi", "Suomeksi");
    // // p.getDescription().put("kieli_sv", "Svensk");
    //
    // p.setName("Name!");
    // p.setPath(path);
    // p.setType(Param.Type.STRING);
    // }
    //
    // ParameterValueString pv = new ParameterValueString();
    // pv.setTarget(target);
    // pv.setValue(value);
    //
    // // p.getValues().add(pv);
    //
    // parameterRepository.save(p);
    // }
    //
    // private void createDemoParameter(String path, String target, int value) {
    //
    // Param p = parameterRepository.findByPath(path);
    //
    // if (p == null) {
    // p = new Param();
    // // p.getDescription().put("kieli_fi", "Suomeksi");
    // // p.getDescription().put("kieli_sv", "Svensk");
    //
    // p.setName("Name!");
    // p.setPath(path);
    // p.setType(Param.Type.INT);
    // }
    //
    // ParameterValueInteger pv = new ParameterValueInteger();
    // pv.setTarget(target);
    // pv.setValue(value);
    //
    // // p.getValues().add(pv);
    //
    // parameterRepository.save(p);
    // }
    //
    // private void createDemoParameter(String path, String target, boolean
    // value) {
    //
    // Param p = parameterRepository.findByPath(path);
    //
    // if (p == null) {
    // p = new Param();
    // // p.getDescription().put("kieli_fi", "Suomeksi");
    // // p.getDescription().put("kieli_sv", "Svensk");
    //
    // p.setName("Name!");
    // p.setPath(path);
    // p.setType(Param.Type.BOOLEAN);
    // }
    //
    // ParameterValueBoolean pv = new ParameterValueBoolean();
    // pv.setTarget(target);
    // pv.setValue(value);
    //
    // p.setValue("" + value);
    //
    // parameterRepository.save(p);
    // }
    //
    // // private void createDemoParameter(String path, String target, Date
    // start, Date end) {
    // //
    // // Param p = parameterRepository.findByPath(path);
    // //
    // // if (p == null) {
    // // p = new Param();
    // //// p.getDescription().put("kieli_fi", "Suomeksi");
    // //// p.getDescription().put("kieli_sv", "Svensk");
    // //
    // // p.setName("Name!");
    // // p.setPath(path);
    // //
    // // p.setValue(start)
    // // if (end == null) {
    // // p.setType(Parameter.Type.DATE);
    // // } else {
    // // p.setType(Parameter.Type.DATE_RANGE);
    // // }
    // // }
    // //
    // // ParameterValue pv;
    // //
    // // if (end == null) {
    // // ParameterValueDate pvx = new ParameterValueDate();
    // // pvx.setValue(start);
    // // pv = pvx;
    // // } else {
    // // ParameterValueDateRange pvx = new ParameterValueDateRange();
    // // pvx.setStart(start);
    // // pvx.setEnd(end);
    // // pv = pvx;
    // // }
    // //
    // // pv.setTarget(target);
    // //
    // // p.getValues().add(pv);
    // //
    // // parameterRepository.save(p);
    // // }
}
