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
package fi.vm.sade.ohjausparametrit.api;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Usage examples.
 *
 * <pre>
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
public interface OhjausparametritResource {

    /**
     * Test method.
     *
     * @return
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    /**
     * List all params (and values).
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ParameterRDTO> list();

    /**
     * List all parameters (and values) in given "sub" category.
     *
     * @param category "prefix"/category
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("{cat}")
    public List<ParameterRDTO> listByCategory(@PathParam("cat") String category);

    /**
     * Get given parameters and values for given target.
     *
     * @param category "prefix" / category
     * @param target if NONE then the "default"/empty categoty is used
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("{cat}/{target}")
    public List<ParameterRDTO> getParameterByCategoryAndtarget(@PathParam("cat") String category, @PathParam("target") String target);
}
