package fi.oph.ohjausparametrit.ovara.ajastus.configurations;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"fi.oph.ohjausparametrit.repository"})
@EntityScan({"fi.oph.ohjausparametrit.model", "fi.oph.ohjausparametrit.ovara.ajastus"})
@EnableTransactionManagement
public class OvaraOhjausparametritConfigurations {
    @Bean
    public AsyncHttpClient commonHttpClient() {
        AsyncHttpClient client = Dsl.asyncHttpClient();
        return client;
    }
}
