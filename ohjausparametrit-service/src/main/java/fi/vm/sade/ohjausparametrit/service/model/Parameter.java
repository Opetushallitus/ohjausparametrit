package fi.vm.sade.ohjausparametrit.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = Parameter.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = {
        Parameter.PATH, Parameter.NAME }))
public class Parameter extends BaseEntity {

    public enum Type {
        BOOLEAN, INT, STRING, DATE
    }

    private static final long serialVersionUID = 1L;
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String TYPE = "type";
    public static final String VALUE = "value";

    public static final String TABLE_NAME = "parameter";

    @Column(name = PATH, nullable = false)
    private String path;

    @Column(name = NAME, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = TYPE, nullable = false)
    private Type type;

    @Column(name = VALUE, nullable = false)
    private String value;

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }
    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
