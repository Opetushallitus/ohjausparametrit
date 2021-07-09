package fi.oph.ohjausparametrit.client;

import com.google.gson.Gson;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.configurations.ConfigEnums;
import fi.oph.ohjausparametrit.configurations.properties.KoutaProperties;
import fi.vm.sade.javautils.nio.cas.CasClient;
import fi.vm.sade.javautils.nio.cas.CasConfig;
import fi.vm.sade.properties.OphProperties;
import java.time.Duration;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KoutaClient {

  private static final Logger logger = LoggerFactory.getLogger(KoutaClient.class);

  private static final Duration AUTHENTICATION_TIMEOUT = Duration.ofSeconds(10);

  private final OphProperties ophProperties;
  private final KoutaProperties koutaProperties;
  private final Gson gson;
  private final CasClient casClient;

  @Autowired
  public KoutaClient(OphProperties ophProperties, KoutaProperties koutaProperties) {

    this.ophProperties = ophProperties;
    this.koutaProperties = koutaProperties;

    logger.info("CAS-URL: {}", ophProperties.url("cas.url"));

    CasConfig casConfig =
        new CasConfig(
            koutaProperties.getUsername(),
            koutaProperties.getPassword(),
            ophProperties.url("cas.url"),
            koutaProperties.getService(),
            ConfigEnums.CALLER_ID.value(),
            ConfigEnums.CALLER_ID.value(),
            koutaProperties.getSessionCookie(),
            koutaProperties.getSecurityUriSuffix(),
            null,
            null);

    this.casClient = new CasClient(casConfig);

    this.gson = new Gson();
  }

  public void test() {
    Request request =
        new RequestBuilder()
            .setMethod("GET")
            .setUrl(ophProperties.url("kouta.haku.search.tarjoaja", "1.2.246.562.10.00000000001"))
            .build();

    try {
      Response response = casClient.executeBlocking(request);
    } catch (Exception e) {
      logger.error("Kouta-internal-pyyntö epäonnistui: ", e);
    }
  }

  public KoutaHaku getHaku(String oid) {
    Request request =
        new RequestBuilder().setMethod("GET").setUrl(ophProperties.url("kouta.haku", oid)).build();

    try {
      Response response = casClient.executeBlocking(request);
      KoutaHaku haku = gson.fromJson(response.getResponseBody(), KoutaHaku.class);
      return haku;
    } catch (Exception e) {
      throw new RuntimeException(
          String.format("Kouta-internal-pyyntö epäonnistui | Request: %s", request), e);
    }
  }
}
