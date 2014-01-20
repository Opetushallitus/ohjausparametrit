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
package fi.vm.sade.ohjausparametrit.service.conversion;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;

/**
 * Kind of stupid since it's 1-1... but just in case.
 * 
 * @author mlyly
 */
public class ConvertParameterToParameterRDTO implements
        Function<Parameter, ParameterRDTO> {

    @Override
    public ParameterRDTO apply(@Nullable Parameter s) {

        if (s == null) {
            return null;
        }

        ParameterRDTO t = new ParameterRDTO();

        t.setCreated(s.getCreated());
        t.setCreatedBy(s.getCreatedBy());
        t.setModified(s.getModified());
        t.setModifiedBy(s.getModifiedBy());
        t.setName(s.getName());
        t.setPath(s.getPath());
        // t.setDescription(s.getDescription());
        // t.setType(s.getType() != null ? s.getType().name() :
        // Parameter.Type.UNKNOWN.name());

        {
            // Get and convert values
            // List<ParameterValueRDTO> dtos = new
            // ArrayList<ParameterValueRDTO>();
            // for (ParameterValue parameterValue : s.getValues()) {
            // dtos.add(getConversionService().convert(parameterValue,
            // ParameterValueRDTO.class));
            // }
            // t.setValues(dtos);
        }

        return t;
    }

}
