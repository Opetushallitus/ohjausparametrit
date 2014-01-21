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
import javax.persistence.MappedSuperclass;

/**
 * Base "entity". Contains basic fields.
 * <pre>
 *   created
 *   createdBy
 *   modified
 *   modifiedBy
 * </pre>
 *
 * @author mlyly
 */
@MappedSuperclass
public class BaseEntity extends fi.vm.sade.generic.model.BaseEntity {

    private static final long serialVersionUID = 1L;
    
    public static final String MODIFIED = "modified";
    public static final String CREATED = "created";
    public static final String MODIFIEDBY = "modifiedBy";
    public static final String CREATEDBY = "createdBy";
    
    
    @Column(name=MODIFIED)
    private Date modified = new Date();
    
    @Column(name=MODIFIEDBY)
    private String modifiedBy;
    
    @Column(name=CREATED)
    private Date created = new Date();
    
    @Column(name=CREATEDBY)
    private String createdBy;

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
