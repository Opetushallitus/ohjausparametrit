package fi.oph.ohjausparametrit.client.dto;

import java.util.List;
import java.util.stream.Stream;

public class OrganisaatioChild {

  private String oid;

  private List<OrganisaatioChild> children;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public List<OrganisaatioChild> getChildren() {
    return children;
  }

  public void setChildren(List<OrganisaatioChild> children) {
    this.children = children;
  }

  public Stream<OrganisaatioChild> flattened() {
    return Stream.concat(Stream.of(this), children.stream().flatMap(OrganisaatioChild::flattened));
  }
}
