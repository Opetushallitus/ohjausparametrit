package fi.oph.ohjausparametrit.service;

import static fi.oph.ohjausparametrit.util.JsonUtil.getJSONAsString;

import fi.oph.ohjausparametrit.audit.OhjausparametritAuditLogger;
import fi.oph.ohjausparametrit.audit.OhjausparametritOperation;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.repository.JSONParameterRepository;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.util.Date;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  public JSONParameter findByTarget(String target) {
    return parameterRepository.findByTarget(target);
  }

  public Iterable<JSONParameter> findAll() {
    return parameterRepository.findAll();
  }

  public void setParameters(String target, String value) {

    // Find existing parameter if any
    JSONParameter p = parameterRepository.findByTarget(target);

    String oldValue = (p == null ? null : p.getJsonValue());
    auditLog(target, value, oldValue);

    if (value == null || value.trim().isEmpty()) {
      if (p != null) {
        parameterRepository.delete(p);
      }
    } else {
      if (p == null) {
        // New target
        p = new JSONParameter();
        p.setTarget(target);
      }
      p.setJsonValue(value);
      parameterRepository.save(p);
    }
  }

  public void setParameters(String target, JSONObject value) {
    if (value != null) {
      try {
        value.put("__modified__", new Date().getTime());
        value.put("__modifiedBy__", SecurityUtil.getCurrentUserName());
      } catch (JSONException ex) {
        logger.error("Failed to set modified / modified by");
      }
    }

    setParameters(target, getJSONAsString(value));
  }

  private void auditLog(String target, String newValue, String oldValue) {
    try {
      OhjausparametritOperation operation;
      Changes changes;
      logger.info("NEW: {} | OLD: {}", newValue, oldValue);
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
