/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.oph.ohjausparametrit.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "parameter")
@AuditTable("parameter_history")
public class JSONParameter {

  @Id private String target;

  @Type(JsonType.class)
  @Column(name = "jsonvalue", columnDefinition = "jsonb")
  private String jsonValue;

  private String muokkaaja;

  private Date muokattu;

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

  public String getMuokkaaja() {
    return muokkaaja;
  }

  public void setMuokkaaja(String muokkaaja) {
    this.muokkaaja = muokkaaja;
  }

  public Date getMuokattu() {
    return muokattu;
  }

  public void setMuokattu(Date muokattu) {
    this.muokattu = muokattu;
  }
}
