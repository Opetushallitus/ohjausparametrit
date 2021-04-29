package fi.oph.ohjausparametrit.client;

import fi.oph.ohjausparametrit.configurations.ConfigEnums;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {

  private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

  private OkHttpClient okHttpClient;

  @Autowired
  public HttpClient(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  public Response get(String url) {
    Request request =
        new Request.Builder().url(url).header("Caller-id", ConfigEnums.CALLER_ID.value()).build();

    Call call = okHttpClient.newCall(request);
    try {
      return callToFuture(call).get();
    } catch (Exception e) {
      logger.error("Request to organisaatiopalvelu failed | Request: {}", request);
      throw new RuntimeException(e);
    }
  }

  private CompletableFuture<Response> callToFuture(Call call) {
    final CompletableFuture<Response> r = new CompletableFuture<>();
    call.enqueue(
        new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            r.completeExceptionally(e);
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            r.complete(response);
          }
        });
    return r;
  }
}
