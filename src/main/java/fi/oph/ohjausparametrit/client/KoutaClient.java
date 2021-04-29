package fi.oph.ohjausparametrit.client;

import com.google.gson.Gson;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.configurations.ConfigEnums;
import fi.oph.ohjausparametrit.configurations.properties.KoutaProperties;
import fi.vm.sade.javautils.cas.CasHttpClient;
import fi.vm.sade.properties.OphProperties;
import java.time.Duration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
  private final OkHttpClient httpClient;
  private final CasHttpClient casHttpClient;
  private final Gson gson;

  @Autowired
  public KoutaClient(
      OphProperties ophProperties, KoutaProperties koutaProperties, OkHttpClient httpClient) {

    this.ophProperties = ophProperties;
    this.koutaProperties = koutaProperties;
    this.httpClient = httpClient;

    this.casHttpClient =
        new CasHttpClient(
            this.httpClient,
            ConfigEnums.CALLER_ID.value(),
            koutaProperties.getSessionCookie(),
            koutaProperties.getService(),
            ophProperties.url("cas.url"),
            koutaProperties.getSecurityUriSuffix(),
            koutaProperties.getUsername(),
            koutaProperties.getPassword(),
            AUTHENTICATION_TIMEOUT);

    this.gson = new Gson();
  }

  public void test() {
    Request request =
        new Request.Builder()
            .url(ophProperties.url("kouta.haku.search.tarjoaja", "1.2.246.562.10.00000000001"))
            .header("Caller-id", ConfigEnums.CALLER_ID.value())
            .header("CSRF", ConfigEnums.CALLER_ID.value())
            .build();

    try {
      Response response = casHttpClient.call(request).get();
      logger.info("RESPONSE: {}", response);
    } catch (Exception e) {
      logger.error("Kouta-internal-pyyntö epäonnistui: ", e);
    }
  }

  public KoutaHaku getHaku(String oid) {
    Request request =
        new Request.Builder()
            .url(ophProperties.url("kouta.haku", oid))
            .header("Caller-id", ConfigEnums.CALLER_ID.value())
            .header("CSRF", ConfigEnums.CALLER_ID.value())
            .build();

    try {
      Response response = casHttpClient.call(request).get();
      logger.info("RESPONSE: {}", response);
      KoutaHaku haku = gson.fromJson(response.body().string(), KoutaHaku.class);
      return haku;
    } catch (Exception e) {
      throw new RuntimeException(
          String.format("Kouta-internal-pyyntö epäonnistui | Request: %s", request), e);
    }
  }
}
