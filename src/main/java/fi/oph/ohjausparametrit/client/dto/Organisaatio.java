package fi.oph.ohjausparametrit.client.dto;

public class Organisaatio {

  private String oid;
  private String parentOidPath;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getParentOidPath() {
    return parentOidPath;
  }

  public void setParentOidPath(String parentOidPath) {
    this.parentOidPath = parentOidPath;
  }

  @Override
  public String toString() {
    return "Organisaatio{"
        + "oid='"
        + oid
        + '\''
        + ", parentOidPath='"
        + parentOidPath
        + '\''
        + '}';
  }
}
