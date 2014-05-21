/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.vm.sade.ohjausparametrit.service.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Simple model for parameter storage - target has attached 
 *
 * @author mlyly
 */
@Entity
@Table(name = JSONParameter.TABLE_NAME)
public class JSONParameter {
    
    public static final String TABLE_NAME = "parameter";
    
    @Id
    private String target;
    
    private String jsonValue;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }
    
}
