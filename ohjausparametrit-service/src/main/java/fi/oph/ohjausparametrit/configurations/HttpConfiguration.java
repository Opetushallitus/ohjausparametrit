package fi.oph.ohjausparametrit.configurations;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfiguration {

  @Bean
  public OkHttpClient getHttpClient() {
    OkHttpClient client = new OkHttpClient.Builder().build();
    return client;
  }
}
