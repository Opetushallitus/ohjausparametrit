package fi.oph.ohjausparametrit.configurations;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"fi.oph.ohjausparametrit.repository"})
@EntityScan({"fi.oph.ohjausparametrit.model"})
@EnableTransactionManagement
public class JpaConfiguration {}
