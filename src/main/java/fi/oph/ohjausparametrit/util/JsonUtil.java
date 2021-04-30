package fi.oph.ohjausparametrit.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class JsonUtil {

  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  public static JSONObject getAsJSON(String data) {
    try {
      if (data == null) {
        return null;
      }
      return new JSONObject(data);
    } catch (JSONException ex) {
      return null;
    }
  }

  public static String getJSONAsString(JSONObject json) {
    try {
      if (json == null) {
        return null;
      }
      return json.toString(2);
    } catch (JSONException ex) {
      return null;
    }
  }

  public static void validateIsJson(String target, String json) {
    if (json == null) return;
    try {
      new JSONObject(json);
    } catch (JSONException ex) {
      String errorMessage =
          String.format(
              "Not valid json: %s | Target: %s | User: %s",
              json, target, SecurityUtil.getCurrentUserName());
      logger.error(errorMessage, ex);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }
  }
}
