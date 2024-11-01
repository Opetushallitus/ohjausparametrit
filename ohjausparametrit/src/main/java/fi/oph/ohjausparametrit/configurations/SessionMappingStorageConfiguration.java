package fi.oph.ohjausparametrit.configurations;

import fi.oph.ohjausparametrit.configurations.security.JdbcSessionMappingStorage;
import fi.oph.ohjausparametrit.configurations.security.OphSessionMappingStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;

@Configuration
public class SessionMappingStorageConfiguration {

  @Bean
  public OphSessionMappingStorage sessionMappingStorage(
      JdbcTemplate jdbcTemplate, JdbcIndexedSessionRepository sessionRepository) {
    return new JdbcSessionMappingStorage(jdbcTemplate, sessionRepository);
  }
}
