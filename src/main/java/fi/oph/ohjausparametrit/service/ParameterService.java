package fi.oph.ohjausparametrit.service;

import static fi.oph.ohjausparametrit.util.JsonUtil.getAsJSON;

import com.google.gson.JsonObject;
import fi.oph.ohjausparametrit.audit.OhjausparametritAuditLogger;
import fi.oph.ohjausparametrit.audit.OhjausparametritOperation;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.repository.JSONParameterRepository;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ParameterService {

  private static final Logger logger = LoggerFactory.getLogger(ParameterService.class);

  private OhjausparametritAuditLogger auditLogger;
  private JSONParameterRepository parameterRepository;

  public ParameterService(
      JSONParameterRepository parameterRepository, OhjausparametritAuditLogger auditLogger) {
    this.parameterRepository = parameterRepository;
    this.auditLogger = auditLogger;
  }

  public String get(String target) {
    JSONParameter jsonParameter = parameterRepository.findByTarget(target);
    if (jsonParameter != null) return jsonParameter.getJsonValue();
    else return null;
  }

  public String getAll() {
    try {
      JsonObject result = new JsonObject();
      for (JSONParameter jSONParameter : parameterRepository.findAll(Sort.by("target"))) {
        result.add(jSONParameter.getTarget(), getAsJSON(jSONParameter.getJsonValue()));
      }
      return result.toString();
    } catch (Exception e) {
      logger.error("Failed to produce json output...?", e);
      throw new RuntimeException(e);
    }
  }

  public String getForOids(List<String> oids) {
    try {
      logger.info("getForOids: {}", oids);
      JsonObject result = new JsonObject();
      List<JSONParameter> dbResult = parameterRepository.findByTargetIn(oids);
      for (JSONParameter jSONParameter : dbResult) {
        result.add(jSONParameter.getTarget(), getAsJSON(jSONParameter.getJsonValue()));
      }
      return result.toString();
    } catch (Exception e) {
      logger.error("Failed to produce json output...?", e);
      throw new RuntimeException(e);
    }
  }

  public void setParameters(String target, String value) {

    JSONParameter parameter = parameterRepository.findByTarget(target);

    String oldValue = (parameter == null ? null : parameter.getJsonValue());

    if (value == null || value.trim().isEmpty()) {
      if (parameter != null) {
        parameterRepository.delete(parameter);
      }
    } else {
      if (parameter == null) {
        parameter = new JSONParameter();
        parameter.setTarget(target);
      }
      parameter.setJsonValue(value);
      parameter.setMuokattu(new Date());
      parameter.setMuokkaaja(SecurityUtil.getCurrentUserName());
      parameterRepository.save(parameter);
    }

    auditLog(target, value, oldValue);
  }

  private void auditLog(String target, String newValue, String oldValue) {
    try {
      OhjausparametritOperation operation;
      Changes changes;
      if (newValue != null && oldValue != null) {
        operation = OhjausparametritOperation.UPDATE;
        changes = Changes.updatedDto(newValue, oldValue);
      } else if (newValue != null && oldValue == null) {
        operation = OhjausparametritOperation.CREATE;
        changes = Changes.addedDto(newValue);
      } else {
        operation = OhjausparametritOperation.DELETE;
        changes = Changes.deleteDto(oldValue);
      }
      Target auditTarget = new Target.Builder().setField("target", target).build();
      auditLogger.log(operation, auditTarget, changes);
    } catch (Exception e) {
      logger.error(
          "Audit logging failed for target {} |User: {} | New: {} | Old: {}",
          target,
          SecurityUtil.getCurrentUserName(),
          newValue,
          oldValue,
          e);
    }
  }
}
