package fi.oph.ohjausparametrit;

import static org.mockito.Mockito.mock;

import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.service.SecurityService;
import fi.oph.ohjausparametrit.service.TestSecurityService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@SpringBootApplication
public class TestApplication {

  @Configuration
  @EnableJpaRepositories(basePackages = "fi.oph.ohjausparametrit.repository")
  @PropertySource("application.yml")
  @EnableTransactionManagement
  public class JpaConfig {}

  @Bean
  public JdbcIndexedSessionRepository jdbcOperationsSessionRepository() {
    return mock(JdbcIndexedSessionRepository.class);
  }

  @Bean
  public KoutaClient koutaClient() {
    return mock(KoutaClient.class);
  }

  @Bean
  public OrganisaatioClient organisaatioClient() {
    return mock(OrganisaatioClient.class);
  }

  @Bean
  @Primary
  SecurityService securityService(KoutaClient koutaClient, OrganisaatioClient organisaatioClient) {
    return new TestSecurityService(koutaClient, organisaatioClient);
  }

  @Bean(name = "mvcHandlerMappingIntrospector")
  public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
    return new HandlerMappingIntrospector();
  }

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
