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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Base class for parameter values.
 *
 * Don't use this class when instantiating actual parameters.
 *
 * Possible values:
 * <ul>
 * <li>Boolean - yes/no value, for example: ("security.logins_allowed", "organisation_SOME_OID) - FALSE -- could disable logins from spesific Organisation.</li>
 * <li>Integer - number value, for example: ("security.max_active_users" "") - 1000 -- could limit logins when this limit is reached.</li>
 * <li>String -- string value</li>
 * <li>Date, date value, for example: ("tarjonta.haku.published", "") - 1.9.2012 10:00, could automatically trigger publish and lock edits in Tarjonta UI</li>
 * <li>DateRange, for example: ("tarjonta.haku.extra_edit_window", "haku_SOME_OID") -- could allow extra edit "window" in Tarjonta UI.</li>
 * </ul>
 *
 * @author mlyly
 */
// @Document(collection = "parameter_values")
public abstract class ParameterValue extends BaseEntity {

    public static final String NO_TARGET = "NONE";

    /**
     * By default no target.
     */
    private String target = NO_TARGET;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        if (target == null || target.isEmpty()) {
            target = NO_TARGET;
        }
        this.target = target;
    }

}
