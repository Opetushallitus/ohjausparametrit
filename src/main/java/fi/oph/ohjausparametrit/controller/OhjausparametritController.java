package fi.oph.ohjausparametrit.controller;

import static fi.oph.ohjausparametrit.util.JsonUtil.*;

import com.google.gson.Gson;
import fi.oph.ohjausparametrit.audit.OhjausparametritAuditLogger;
import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.service.ParameterService;
import fi.oph.ohjausparametrit.service.SecurityService;
import fi.oph.ohjausparametrit.util.JsonUtil;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import io.swagger.annotations.Api;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

  private Gson gson = new Gson();

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
    KoutaHaku haku = koutaClient.getHaku("1.2.246.562.29.00000000000000000001");
    logger.info("Kouta: {} {}", haku.getOid(), haku.getOrganisaatioOid());
    organisaatioClient.getChildOids("1.2.246.562.10.53642770753");
  }

  @GetMapping(value = "/ALL", produces = "application/json; charset=utf-8")
  public String doGetAll() {
    return parameterService.getAll();
  }

  @PostMapping(value = "/ALL")
  public String doGetAllPost() {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, "Not allowed to save ohjausparametrit fro target ALL!");
  }

  @GetMapping(value = "/{target}", produces = "application/json; charset=utf-8")
  public String doGet(@PathVariable String target) {
    String response = parameterService.get(target);
    if (response == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "target not found");
    return parameterService.get(target);
  }

  @PostMapping(value = "/{target}")
  @PreAuthorize(
      "hasAnyRole('ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA', 'APP_KOUTA_HAKU_CRUD', 'APP_KOUTA_HAKU_READ_UPDATE')")
  public void doPost(@PathVariable String target, @RequestBody String value) {
    logger.info(
        "Saving ohjausparmetrit for target {} by {} [ {}",
        target,
        SecurityUtil.getCurrentUserName(),
        value);
    JsonUtil.validateIsJson(target, value);
    if (!securityService.isAuthorizedToModifyHaku(
        target,
        Arrays.asList(
            "ROLE_APP_KOUTA_OPHPAAKAYTTAJA", "APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_READ_UPDATE")))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    if (value == null || value.trim().isEmpty()) {
      parameterService.setParameters(target, null);
    } else {
      parameterService.setParameters(target, value);
    }
  }
}
