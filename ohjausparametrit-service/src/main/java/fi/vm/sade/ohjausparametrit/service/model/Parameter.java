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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author mlyly
 */
@Entity
@Table(name = Parameter.TABLE_NAME, uniqueConstraints = {
    // @UniqueConstraint(name = "UK_haku_01", columnNames = {"oid"})
})
public class Parameter extends BaseEntity {

    public static final String TABLE_NAME = "parametri";

    @Id
    @Column(nullable = false, insertable = true, updatable = false, unique = true)
    private String path;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean required = Boolean.FALSE;

    //    @Column(nullable = false)
    //    @Enumerated(EnumType.STRING)
    //    private ParameterTypeEnum type;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

}
