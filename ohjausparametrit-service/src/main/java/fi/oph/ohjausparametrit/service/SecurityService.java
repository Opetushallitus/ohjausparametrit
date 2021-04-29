package fi.oph.ohjausparametrit.service;

import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.util.KoutaUtil;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  private static Logger LOG = LoggerFactory.getLogger(SecurityService.class);

  private static final String ORGANISATION_OID_PREFIX = "1.2.246.562.10";

  private KoutaClient koutaClient;
  private OrganisaatioClient organisaatioClient;

  public SecurityService(KoutaClient koutaClient, OrganisaatioClient organisaatioClient) {
    this.koutaClient = koutaClient;
    this.organisaatioClient = organisaatioClient;
  }

  public boolean isAuthorizedToModifyHaku(String hakuOid, List<String> requiredRoles) {
    if (!KoutaUtil.isKoutaHakuOid(hakuOid)) return true;
    KoutaHaku haku = koutaClient.getHaku(hakuOid);
    if (haku == null) return false;
    List<String> hakuOrgansationOids = organisaatioClient.getChildOids(haku.getOrganisaatio());
    List<String> roleOrganisationOids = getOrganisationOidsForAuthentication(requiredRoles);
    return roleOrganisationOids.stream().anyMatch(o -> hakuOrgansationOids.contains(o));
  }

  private List<String> getOrganisationOidsForAuthentication(List<String> requiredRoles) {
    List<String> roles =
        getRolesFromAuthentication().stream()
            .filter(r -> requiredRoles.stream().anyMatch(vr -> r.contains(vr)))
            .collect(Collectors.toList());
    List<String> organisationOids =
        roles.stream()
            .map(this::parseOrganisationOidFromSecurityRole)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    return organisationOids;
  }

  private List<String> getRolesFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof CasAuthenticationToken) {
      CasAuthenticationToken casAuthenticationToken = (CasAuthenticationToken) authentication;
      return casAuthenticationToken.getAuthorities().stream()
          .map(a -> a.getAuthority())
          .collect(Collectors.toList());
    } else {
      LOG.error(
          "Tried to get authorities from spring authentication token but token wasn't CAS authentication token");
      return Collections.emptyList();
    }
  }

  private Optional<String> parseOrganisationOidFromSecurityRole(String role) {
    return parseOidFromSecurityRole(role, ORGANISATION_OID_PREFIX);
  }

  private Optional<String> parseOidFromSecurityRole(String role, String prefix) {
    String[] pieces = StringUtils.trimToEmpty(role).split("_");
    if (pieces.length > 0) {
      String lastPiece = pieces[pieces.length - 1];
      if (lastPiece.startsWith(prefix)) {
        return Optional.of(lastPiece);
      } else {
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }
}
