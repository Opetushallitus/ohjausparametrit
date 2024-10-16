package fi.oph.ohjausparametrit.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.oph.ohjausparametrit.client.dto.OrganisaatioChild;
import fi.oph.ohjausparametrit.client.dto.OrganisaatioChildren;
import fi.vm.sade.properties.OphProperties;
import java.util.List;
import java.util.stream.Collectors;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisaatioClient {

  private static final Logger logger = LoggerFactory.getLogger(OrganisaatioClient.class);

  private HttpClient httpClient;
  private OphProperties ophProperties;
  private Gson gson;

  @Autowired
  public OrganisaatioClient(HttpClient httpClient, OphProperties ophProperties) {
    this.httpClient = httpClient;
    this.ophProperties = ophProperties;
    this.gson = new Gson();
  }

  public List<OrganisaatioChild> getChildren(String oid) {
    Response response = httpClient.get(ophProperties.url("organisaatio.jalkelaiset", oid));
    try {
      String body = response.getResponseBody();
      OrganisaatioChildren dto =
          gson.fromJson(body, new TypeToken<OrganisaatioChildren>() {}.getType());
      return dto.getOrganisaatiot();
    } catch (Exception e) {
      throw new RuntimeException(
          String.format("Getting organisaatio children failed for %s", oid), e);
    }
  }

  public List<String> getChildOids(String oid) {
    return getChildren(oid).stream()
        .flatMap(OrganisaatioChild::flattened)
        .map(org -> org.getOid())
        .collect(Collectors.toList());
  }
}
