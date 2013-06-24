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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author mlyly
 */
@Document(collection="parameters")
public class Parameter extends BaseEntity {

    public enum Type {
        UNKNOWN, BOOLEAN, INTEGER, STRING, DATE, DATE_RANGE;
    };

    @Id
    private String path;

    private String name;
    private Type type = Type.UNKNOWN;
    private boolean required = Boolean.FALSE;
    private Map<String, String> description;
    private List<ParameterValue> values;

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

    public Map<String, String> getDescription() {
        if (description == null) {
            description = new HashMap<String, String>();
        }
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public List<ParameterValue> getValues() {
        if (values == null) {
            values = new ArrayList<ParameterValue>();
        }
        return values;
    }

    public void setValues(List<ParameterValue> values) {
        this.values = values;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
