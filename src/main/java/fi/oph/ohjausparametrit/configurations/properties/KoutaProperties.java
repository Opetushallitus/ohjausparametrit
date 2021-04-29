package fi.oph.ohjausparametrit.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kouta")
public class KoutaProperties {

  private String username;
  private String password;
  private String service;
  private String sessionCookie;
  private String securityUriSuffix;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getSessionCookie() {
    return sessionCookie;
  }

  public void setSessionCookie(String sessionCookie) {
    this.sessionCookie = sessionCookie;
  }

  public String getSecurityUriSuffix() {
    return securityUriSuffix;
  }

  public void setSecurityUriSuffix(String securityUriSuffix) {
    this.securityUriSuffix = securityUriSuffix;
  }
}
