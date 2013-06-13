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
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * <pre>
 * /.../parametri
 * /.../parametri/hello
 * /.../parametri/this.is.a.test.param
 * /.../parametri/this.is.a.test.param/haku_1.2.3.4.5.874987418742938
 * </pre>
 *
 * @author mlyly
 */
@Path("/parametri")
public interface OhjausparametritResource {

    /**
     *
     * @return
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    /**
     *
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ParameterRDTO> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);

    /**
     *
     * @param path
     * @return
     */
    @GET
    @Path("{path}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ParameterRDTO findByPath(@PathParam("path") String path);

    /**
     *
     * @param path
     * @param target
     * @return
     */
    @GET
    @Path("{path}/{target}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ParameterRDTO findByPathAndTarget(@PathParam("path") String path, @PathParam("target") String target);
}
