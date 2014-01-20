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
package fi.vm.sade.ohjausparametrit.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 
 * @author mlyly
 */
public class ParameterRDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date created;
    private String createdBy;
    private Date modified;
    private String modifiedBy;

    private String path;
    private String name;
    private String type;
    private String value;

    private Map<String, String> description;

    public Date getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public Date getModified() {
        return modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
