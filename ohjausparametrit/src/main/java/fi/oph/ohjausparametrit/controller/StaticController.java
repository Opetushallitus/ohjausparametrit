package fi.oph.ohjausparametrit.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticController implements WebMvcConfigurer {

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addRedirectViewController("/swagger/**", "/swagger-ui/index.html");
    registry.addRedirectViewController("/swagger", "/swagger-ui/index.html");
  }
}
