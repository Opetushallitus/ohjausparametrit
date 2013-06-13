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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.apache.commons.lang.builder.ToStringBuilder;

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
@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("dummy")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "parametri_arvot")
public class ParameterValue extends BaseEntity {

    @EmbeddedId
    private ParameterValuePK id;

    public ParameterValuePK getId() {
        return id;
    }

    public void setId(ParameterValuePK id) {
        this.id = id;
    }

    public String getPath() {
        return getId().getPath();
    }

    public void setPath(String v) {
        getId().setPath(v);
    }

    public String getTarget() {
        return getId().getTarget();
    }

    public void setTarget(String v) {
        getId().setTarget(v);
    }

}
