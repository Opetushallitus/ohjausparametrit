package fi.oph.ohjausparametrit.configurations;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfiguration {

  @Bean
  public AsyncHttpClient commonHttpClient() {
    AsyncHttpClient client = Dsl.asyncHttpClient();
    return client;
  }
}
