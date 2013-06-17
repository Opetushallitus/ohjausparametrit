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

import fi.vm.sade.ohjausparametrit.api.model.ParameterValueBooleanRDTO;
import fi.vm.sade.ohjausparametrit.api.model.ParameterValueDateRDTO;
import fi.vm.sade.ohjausparametrit.api.model.ParameterValueDateRangeRDTO;
import fi.vm.sade.ohjausparametrit.api.model.ParameterValueIntegerRDTO;
import fi.vm.sade.ohjausparametrit.api.model.ParameterValueRDTO;
import fi.vm.sade.ohjausparametrit.api.model.ParameterValueStringRDTO;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValue;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueBoolean;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueDate;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueDateRange;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueInteger;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValueString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * Kind of stupid since it's 1-1... but just in case.
 *
 * @author mlyly
 */
public class ConvertParameterValueToParameterValueRDTO implements Converter<ParameterValue, ParameterValueRDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(ConvertParameterValueToParameterValueRDTO.class);

    @Override
    public ParameterValueRDTO convert(ParameterValue s) {
        if (s == null) {
            return null;
        }

        ParameterValueRDTO t = null;

        if (s instanceof ParameterValueBoolean) {
            ParameterValueBoolean v = (ParameterValueBoolean) s;
            t = new ParameterValueBooleanRDTO(v.getValue());
        } else if (s instanceof ParameterValueDate) {
            ParameterValueDate v = (ParameterValueDate) s;
            t = new ParameterValueDateRDTO(v.getValue());
        } else if (s instanceof ParameterValueDateRange) {
            ParameterValueDateRange v = (ParameterValueDateRange) s;
            t = new ParameterValueDateRangeRDTO(v.getStart(), v.getEnd());
        } else if (s instanceof ParameterValueInteger) {
            ParameterValueInteger v = (ParameterValueInteger) s;
            t = new ParameterValueIntegerRDTO(v.getValue());
        } else if (s instanceof ParameterValueString) {
            ParameterValueString v = (ParameterValueString) s;
            t = new ParameterValueStringRDTO(v.getValue());
        } else {
            t = null;
            LOG.error("Failed to convert: {} to ParameterValueRDTO...", s);
        }

        //
        // Common values
        //
        if (t != null) {
            t.setCreated(s.getCreated());
            t.setCreatedBy(s.getCreatedBy());
            t.setModified(s.getModified());
            t.setModifiedBy(s.getModifiedBy());
            t.setPath(null);
            t.setTarget(s.getTarget());
        }

        return t;
    }
}
