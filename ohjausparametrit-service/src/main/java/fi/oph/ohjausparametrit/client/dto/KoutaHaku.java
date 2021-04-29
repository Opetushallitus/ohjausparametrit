package fi.oph.ohjausparametrit.client.dto;

public class KoutaHaku {

  public KoutaHaku() {}

  public KoutaHaku(String oid, String organisaatio) {
    this.oid = oid;
    this.organisaatio = organisaatio;
  }

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
