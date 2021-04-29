package fi.oph.ohjausparametrit.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonUtil {

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
}
