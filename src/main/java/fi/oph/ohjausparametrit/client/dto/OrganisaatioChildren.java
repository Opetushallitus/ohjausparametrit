package fi.oph.ohjausparametrit.client.dto;

import java.util.List;

public class OrganisaatioChildren {

  private List<OrganisaatioChild> organisaatiot;

  public List<OrganisaatioChild> getOrganisaatiot() {
    return organisaatiot;
  }

  public void setOrganisaatiot(List<OrganisaatioChild> organisaatiot) {
    this.organisaatiot = organisaatiot;
  }
}
