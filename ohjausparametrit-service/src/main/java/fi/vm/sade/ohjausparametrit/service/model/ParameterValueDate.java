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
package fi.vm.sade.ohjausparametrit.service.model;

import java.util.Date;

/**
 *
 * @author mlyly
 * @see Parameter
 * @see ParameterValue
 */
public class ParameterValueDate extends ParameterValue {

    public ParameterValueDate() {
        super();
    }

    public ParameterValueDate(Date v) {
        this();
        start = v;
    }

    private Date start = null;

    public Date getValue() {
        return start;
    }

    public void setValue(Date value) {
        this.start = value;
    }
}
