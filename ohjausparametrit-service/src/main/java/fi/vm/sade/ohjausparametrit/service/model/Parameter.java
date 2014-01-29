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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = Parameter.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = {
        Parameter.PATH, Parameter.NAME }))
public class Parameter extends BaseEntity {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String VALUE = "value";

    public static final String TABLE_NAME = "parameter";

    @Column(name = PATH, nullable = false)
    private String path;

    @Column(name = NAME, nullable = false)
    private String name;

    @Column(name = VALUE, nullable = true)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PATH, insertable = false, updatable = false, referencedColumnName = Template.PATH)
    Template template;

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public <T> T getValue() {
        return (T) deserialize(value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setValue(Object value) {
        this.value = serialize(value);
    }

    /**
     * Serializes actual data into string column in db
     * @param o
     * @return
     */
    private String serialize(Object o) {
        
        if(o==null) {
            return null;
        }

        if (o instanceof String) {
            return "s" + o.toString();
        }

        if (o instanceof Integer || o instanceof Long) {
            return "i" + o.toString();
        }

        if (o instanceof Date) {
            return "d" + ((Date) o).getTime();
        }

        if (o instanceof Boolean) {
            return "b" + o.toString();
        }

        throw new RuntimeException("Don't know how to serialize type: "
                + o.getClass());
    }

    /**
     * Deserializes actual data from string column
     * @param value
     * @return
     */
    private Object deserialize(String value) {

        if (value == null)
            return null;
        if (value.length() < 1)
            return null; // type + payload

        final char c = value.charAt(0);
        final String rawValue = value.substring(1);

        switch (c) {
        case 's':
            return rawValue;
        case 'i':
            return Long.valueOf(rawValue);
        case 'd':
            return new Date(Long.parseLong(rawValue));
        case 'b':
            return Boolean.valueOf(rawValue);
        default:
            return value;
        }

    }

    @Override
    public String toString() {
        return "Parameter [path=" + path + ", name=" + name + ", value="
                + value + ", toString()=" + super.toString() + "]";
    }

}
