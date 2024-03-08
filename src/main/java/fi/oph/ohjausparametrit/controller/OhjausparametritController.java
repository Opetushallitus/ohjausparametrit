package fi.oph.ohjausparametrit.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.oph.ohjausparametrit.audit.OhjausparametritAuditLogger;
import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.service.ParameterService;
import fi.oph.ohjausparametrit.service.SecurityService;
import fi.oph.ohjausparametrit.service.SiirtotiedostoService;
import fi.oph.ohjausparametrit.util.JsonUtil;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import io.swagger.annotations.Api;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/rest/parametri")
@Api(tags = "Ohjausparametreihin liittyvät operaatiot")
@Transactional
public class OhjausparametritController {

  private static final Logger logger = LoggerFactory.getLogger(OhjausparametritController.class);

  private OhjausparametritAuditLogger auditLogger;

  private ParameterService parameterService;
  private SecurityService securityService;
  private SiirtotiedostoService siirtotiedostoService;
  private KoutaClient koutaClient;
  private OrganisaatioClient organisaatioClient;

  private static String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  private static ZoneId TIMEZONE = ZoneId.of("Europe/Helsinki");
  private DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern(DATETIME_FORMAT).withZone(TIMEZONE);

  public OhjausparametritController(
      ParameterService parameterService,
      SecurityService securityService,
      SiirtotiedostoService siirtotiedostoService,
      OhjausparametritAuditLogger auditLogger,
      OrganisaatioClient organisaatioClient,
      KoutaClient koutaClient) {
    this.parameterService = parameterService;
    this.securityService = securityService;
    this.siirtotiedostoService = siirtotiedostoService;
    this.auditLogger = auditLogger;
    this.organisaatioClient = organisaatioClient;
    this.koutaClient = koutaClient;
  }

  @GetMapping(value = "/authorize")
  @PreAuthorize(
      "hasAnyRole('ROLE_APP_TARJONTA_READ', 'ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA', 'APP_KOUTA_HAKU_CRUD', 'APP_KOUTA_HAKU_READ_UPDATE')")
  public String doAuthorize() {
    return SecurityUtil.getCurrentUserName();
  }

  @GetMapping(value = "/authorize/{target}")
  @PreAuthorize(
      "hasAnyRole('ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA', 'APP_KOUTA_HAKU_CRUD', 'APP_KOUTA_HAKU_READ_UPDATE')")
  public String doAuthorizeForTarget(@PathVariable String target) {
    if (!securityService.isAuthorizedToModifyHaku(
        target,
        Arrays.asList(
            "ROLE_APP_KOUTA_OPHPAAKAYTTAJA", "APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_READ_UPDATE")))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    return SecurityUtil.getCurrentUserName();
  }

  @GetMapping(value = "/ALL", produces = "application/json; charset=utf-8")
  public String doGetAll() {
    return parameterService.getAll();
  }

  @PostMapping(value = "/ALL")
  public String doGetAllPost() {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, "Not allowed to save ohjausparametrit for target ALL!");
  }

  @PostMapping(value = "/oids", produces = "application/json; charset=utf-8")
  public String doPostForOids(@RequestBody List<String> oids) {
    return parameterService.getForOids(oids);
  }

  @GetMapping(value = "/{target}", produces = "application/json; charset=utf-8")
  public String doGet(@PathVariable String target) {
    String response = parameterService.get(target);
    if (response == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "target not found");
    return parameterService.get(target);
  }

  private Date parseDateTime(String dateTimeStr, String fieldName, ZonedDateTime defaultDateTime) {
    try {
      ZonedDateTime dateTime =
          isBlank(dateTimeStr) ? null : ZonedDateTime.parse(dateTimeStr, dateTimeFormatter);
      if (dateTime != null) {
        return Date.from(dateTime.toInstant());
      } else if (defaultDateTime != null) {
        return Date.from(defaultDateTime.toInstant());
      }
      return null;
    } catch (DateTimeParseException dtpe) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format(
              "Illegal value for '%s', allowed format is '%s'", fieldName, DATETIME_FORMAT));
    }
  }

  @GetMapping(value = "siirtotiedosto", produces = "application/json; charset=utf-8")
  public String doGet(
      @RequestParam(required = false) String startDatetime,
      @RequestParam(required = false) String endDatetime) {
    Date start = parseDateTime(startDatetime, "startDateTime", null);
    Date end = parseDateTime(endDatetime, "endDateTime", ZonedDateTime.now(TIMEZONE));
    int partitionSize = siirtotiedostoService.getMaxItemcountInTransferFile();
    int page = 0;
    List<String> keys = new ArrayList<>();
    int total = 0;

    List<JSONParameter> dbResults =
        parameterService.getPartitionByModifyDatetime(start, end, partitionSize, page++);
    while (!dbResults.isEmpty()) {
      total += dbResults.size();
      keys.add(siirtotiedostoService.createSiirtotiedosto(startDatetime, endDatetime, dbResults));
      dbResults = parameterService.getPartitionByModifyDatetime(start, end, partitionSize, page++);
    }

    JsonObject result = new JsonObject();
    JsonArray keyJson = new JsonArray();
    keys.forEach(key -> keyJson.add(key));
    result.add("keys", keyJson);
    result.addProperty("total", total);
    result.addProperty("success", true);
    return result.toString();
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
