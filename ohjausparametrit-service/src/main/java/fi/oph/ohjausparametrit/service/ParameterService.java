package fi.oph.ohjausparametrit.service;

import static fi.oph.ohjausparametrit.util.JsonUtil.getJSONAsString;

import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.repository.JSONParameterRepository;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import java.util.Date;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParameterService {

  private static final Logger LOG = LoggerFactory.getLogger(ParameterService.class);

  @Autowired private JSONParameterRepository parameterRepository;

  public JSONParameter findByTarget(String target) {
    return parameterRepository.findByTarget(target);
  }

  public Iterable<JSONParameter> findAll() {
    return parameterRepository.findAll();
  }

  public void setParameters(String target, String value) {

    // Find existing parameter if any
    JSONParameter p = parameterRepository.findByTarget(target);

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
        LOG.error("Failed to set modified / modified by");
      }
    }

    setParameters(target, getJSONAsString(value));
  }
}
