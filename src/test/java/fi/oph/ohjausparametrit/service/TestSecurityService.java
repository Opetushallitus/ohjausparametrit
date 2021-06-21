package fi.oph.ohjausparametrit.service;

import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TestSecurityService extends SecurityService {

  @Autowired
  public TestSecurityService(KoutaClient koutaClient, OrganisaatioClient organisaatioClient) {
    super(koutaClient, organisaatioClient);
  }

  @Override
  protected List<String> getRolesFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getAuthorities().stream()
        .map(a -> a.getAuthority())
        .collect(Collectors.toList());
  }
}
