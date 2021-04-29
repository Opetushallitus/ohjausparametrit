package fi.oph.ohjausparametrit.client.dto;

public class KoutaHaku {

  private String oid;
  private String organisaatio;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getOrganisaatio() {
    return organisaatio;
  }

  public void setOrganisaatio(String organisaatio) {
    this.organisaatio = organisaatio;
  }
}
