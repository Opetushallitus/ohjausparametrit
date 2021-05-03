package fi.oph.ohjausparametrit.client.dto;

public class KoutaHaku {

  public KoutaHaku() {}

  public KoutaHaku(String oid, String organisaatio) {
    this.oid = oid;
    this.organisaatioOid = organisaatioOid;
  }

  private String oid;
  private String organisaatioOid;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getOrganisaatioOid() {
    return organisaatioOid;
  }

  public void setOrganisaatio(String organisaatio) {
    this.organisaatioOid = organisaatioOid;
  }
}
