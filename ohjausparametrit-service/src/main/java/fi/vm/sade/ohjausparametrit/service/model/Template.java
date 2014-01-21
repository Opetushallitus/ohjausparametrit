package fi.vm.sade.ohjausparametrit.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = Template.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = {
        Template.PATH}))
public class Template extends BaseEntity {

    public enum Type {
        BOOLEAN, INT, STRING, DATE
    }
    
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "template";
    public static final String TYPE = "type";
    public static final String PATH = "path";
    public static final String REQUIRED = "required";

    @Column(name = PATH, nullable = false)
    private String path;
    @Column(name = REQUIRED, nullable = false)
    private boolean required;
    @Enumerated(EnumType.STRING)
    @Column(name = TYPE, nullable = false)
    private Type type;
    public String getPath() {
        return path;
    }
    public Type getType() {
        return type;
    }
    public boolean isRequired() {
        return required;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public void setType(Type type) {
        this.type = type;
    }

}
