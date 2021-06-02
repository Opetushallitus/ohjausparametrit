package fi.oph.ohjausparametrit.service;

import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.util.KoutaUtil;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  private static Logger logger = LoggerFactory.getLogger(SecurityService.class);

  private static final String ORGANISATION_OID_PREFIX = "1.2.246.562.10";

  private KoutaClient koutaClient;
  private OrganisaatioClient organisaatioClient;

  @Autowired
  public SecurityService(KoutaClient koutaClient, OrganisaatioClient organisaatioClient) {
    this.koutaClient = koutaClient;
    this.organisaatioClient = organisaatioClient;
  }

  public boolean isAuthorizedToModifyHaku(String hakuOid, List<String> requiredRoles) {
    if (isSuperuser()) {
      logger.info(
          "User {} is super user. Modifying ohjausparametrit ({}) allowed",
          SecurityUtil.getCurrentUserName(),
          hakuOid);
      return true;
    }
    if (!KoutaUtil.isKoutaHakuOid(hakuOid)) {
      logger.info(
          "Haku {} is not kouta-haku. User {} allowed to modify ohjausparametrit",
          hakuOid,
          SecurityUtil.getCurrentUserName());
      return true;
    }
    KoutaHaku haku = koutaClient.getHaku(hakuOid);
    if (haku == null) {
      logger.info(
          "Haku {} is not found from Kouta. User {} ais not allowed to modify ohjausparametrit",
          hakuOid,
          SecurityUtil.getCurrentUserName());
      return false;
    }
    logger.info(
        "Authorization for user {}, haku {} | Organisaatiot of haku: {}",
        SecurityUtil.getCurrentUserName(),
        hakuOid,
        haku.getOrganisaatioOid());
    List<String> roleOrganisaatioOids = getOrganisaatioOidsForAuthentication(requiredRoles);
    logger.info(
        "Authorization for user {}, haku {} | Organisaatiot of required roles: {}",
        SecurityUtil.getCurrentUserName(),
        hakuOid,
        roleOrganisaatioOids);
    boolean isAllowed = roleOrganisaatioOids.contains(haku.getOrganisaatioOid());
    if (isAllowed) {
      logger.info(
          "Authorization for user {}, haku {} | User is allowed to modify ohjausparametrit",
          SecurityUtil.getCurrentUserName(),
          hakuOid);
    } else {
      logger.info(
          "Authorization for user {}, haku {} | User is not allowed to modify ohjausparametrit",
          SecurityUtil.getCurrentUserName(),
          hakuOid);
    }
    return isAllowed;
  }

  private List<String> getOrganisaatioOidsForAuthentication(List<String> requiredRoles) {
    List<String> roles =
        getRolesFromAuthentication().stream()
            .filter(r -> requiredRoles.stream().anyMatch(vr -> r.contains(vr)))
            .collect(Collectors.toList());
    List<String> organisaatioOids =
        roles.stream()
            .map(this::parseOrganisaatioOidFromSecurityRole)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    List<String> childOrganisaatioOids =
        organisaatioOids.stream()
            .map(organisaatioClient::getChildOids)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    return Stream.concat(organisaatioOids.stream(), childOrganisaatioOids.stream())
        .distinct()
        .collect(Collectors.toList());
  }

  private boolean isSuperuser() {
    List<String> roles = getRolesFromAuthentication();
    return roles.stream().anyMatch(r -> r.contains("APP_KOUTA_OPHPAAKAYTTAJA"));
  }

  protected List<String> getRolesFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof CasAuthenticationToken) {
      CasAuthenticationToken casAuthenticationToken = (CasAuthenticationToken) authentication;
      return casAuthenticationToken.getAuthorities().stream()
          .map(a -> a.getAuthority())
          .collect(Collectors.toList());
    } else {
      logger.error(
          "Tried to get authorities from spring authentication token but token wasn't CAS authentication token");
      return Collections.emptyList();
    }
  }

  private Optional<String> parseOrganisaatioOidFromSecurityRole(String role) {
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
