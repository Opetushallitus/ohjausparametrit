package fi.oph.ohjausparametrit.client;

import fi.oph.ohjausparametrit.configurations.ConfigEnums;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {

  private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

  private AsyncHttpClient client;

  @Autowired
  public HttpClient(AsyncHttpClient client) {
    this.client = client;
  }

  public Response get(String url) {
    Request request =
        new RequestBuilder()
            .setMethod("GET")
            .setUrl(url)
            .setHeader("Caller-id", ConfigEnums.CALLER_ID.value())
            .build();

    try {
      Response response = client.executeRequest(request).get();
      return response;
    } catch (Exception e) {
      logger.error("Request to organisaatiopalvelu failed | Request: {}", request);
      throw new RuntimeException(e);
    }
  }
}
