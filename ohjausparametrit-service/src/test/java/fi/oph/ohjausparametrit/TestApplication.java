package fi.oph.ohjausparametrit;

import static org.mockito.Mockito.mock;

import fi.oph.ohjausparametrit.service.SecurityService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.jdbc.JdbcOperationsSessionRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class TestApplication {

  @Configuration
  @EnableJpaRepositories(basePackages = "fi.oph.ohjausparametrit.repository")
  @PropertySource("application.yml")
  @EnableTransactionManagement
  public class H2JpaConfig {}

  @Bean
  public JdbcOperationsSessionRepository jdbcOperationsSessionRepository() {
    return mock(JdbcOperationsSessionRepository.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
