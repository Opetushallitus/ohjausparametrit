package fi.vm.sade.ohjausparametrit.service;

public class Param {
    
    Object value;
    
    public Param() {
        // for json serialization
    }
    
    public Param(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


}
