package fi.oph.ohjausparametrit.controller;

import static fi.oph.ohjausparametrit.util.JsonUtil.*;

import fi.oph.ohjausparametrit.audit.OhjausparametritAuditLogger;
import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.service.ParameterService;
import fi.oph.ohjausparametrit.service.SecurityService;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import io.swagger.annotations.Api;
import java.util.Arrays;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Simpler parameters:
 *
 * <pre>
 * GET  /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING -> json of all parameters
 *
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING <- json of all parameters updated/inserted/removed (replaces existing)
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING/PARAM_NAME <- json of single parameter updated/inserted/removed
 * </pre>
 */
@RestController
@RequestMapping(value = "/api/v1/rest/parametri")
@Api(tags = "Henkilöön liittyvät operaatiot")
@Transactional
public class OhjausparametritController {

  private static final Logger logger = LoggerFactory.getLogger(OhjausparametritController.class);

  private OhjausparametritAuditLogger auditLogger;

  private ParameterService parameterService;
  private SecurityService securityService;

  private KoutaClient koutaClient;
  private OrganisaatioClient organisaatioClient;

  public OhjausparametritController(
      ParameterService parameterService,
      SecurityService securityService,
      OhjausparametritAuditLogger auditLogger,
      OrganisaatioClient organisaatioClient,
      KoutaClient koutaClient) {
    this.parameterService = parameterService;
    this.securityService = securityService;
    this.auditLogger = auditLogger;
    this.organisaatioClient = organisaatioClient;
    this.koutaClient = koutaClient;
  }

  @GetMapping("/authorize")
  @PreAuthorize(
      "hasAnyRole('ROLE_APP_TARJONTA_READ', 'ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA')")
  public String doAuthorize() {
    return SecurityUtil.getCurrentUserName();
  }

  @GetMapping("/test")
  public void test() {
    koutaClient.test();
    organisaatioClient.getChildOids("1.2.246.562.10.53642770753");
  }

  /** @return all parameters to all targets as JSON. */
  @GetMapping(value = "/ALL", produces = "application/json; charset=utf-8")
  public String doGetAll() {
    try {
      JSONObject result = new JSONObject();
      for (JSONParameter jSONParameter : parameterService.findAll()) {
        result.put(jSONParameter.getTarget(), getAsJSON(jSONParameter.getJsonValue()));
      }
      return result.toString();
    } catch (JSONException ex) {
      logger.error("Failed to produce json output...?", ex);
      throw new RuntimeException(ex);
    }
  }

  /**
   * Load parameters for given target.
   *
   * @param target ex. Haku oid
   * @return parameters as JSON
   */
  @GetMapping(value = "/{target}", produces = "application/json; charset=utf-8")
  public String doGet(@PathVariable String target) {
    JSONParameter parameter = parameterService.findByTarget(target);
    if (parameter == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "target not found");
    }
    return parameter.getJsonValue();
  }

  /**
   * Save parameters for given "target"
   *
   * @param target ex. Haku oid
   * @param value parameters (multiple) of given target as json
   * @return success == OK, failure NOT_ACCEPTABLE or UNAUTHORIZED
   */
  @PostMapping("/{target}")
  @PreAuthorize(
      "hasAnyRole('ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA', 'APP_KOUTA_HAKU_CRUD', 'APP_KOUTA_HAKU_READ_UPDATE')")
  public void doPost(@PathVariable String target, @RequestBody String value) {
    if (!securityService.isAuthorizedToModifyHaku(
        target,
        Arrays.asList(
            "ROLE_APP_KOUTA_OPHPAAKAYTTAJA", "APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_READ_UPDATE")))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    if (value == null || value.trim().isEmpty()) {
      parameterService.setParameters(target, (String) null);
    } else {
      JSONObject json = getAsJSON(value);
      if (json == null) {
        logger.error("Could not parse json for {}: {}", target, value);
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
      }
      parameterService.setParameters(target, json);
    }
  }
}
