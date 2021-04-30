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
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.jdbc.JdbcOperationsSessionRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootApplication
public class TestApplication {

  public static PostgreSQLContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:11.1")
          .withDatabaseName("ohjausparametrit")
          .withUsername("oph")
          .withPassword("oph");

  @Configuration
  @EnableJpaRepositories(basePackages = "fi.oph.ohjausparametrit.repository")
  @PropertySource("application.yml")
  @EnableTransactionManagement
  public class H2JpaConfig {}

  @Bean
  public JdbcOperationsSessionRepository jdbcOperationsSessionRepository() {
    return mock(JdbcOperationsSessionRepository.class);
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
  SecurityService securityService(KoutaClient koutaClient, OrganisaatioClient organisaatioClient) {
    return new TestSecurityService(koutaClient, organisaatioClient);
  }

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
