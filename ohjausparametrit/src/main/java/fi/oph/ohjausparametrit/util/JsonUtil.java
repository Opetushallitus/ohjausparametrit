package fi.oph.ohjausparametrit.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import fi.oph.ohjausparametrit.model.JSONParameter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class JsonUtil {

  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  private static final Gson gson = new Gson();

  public static JsonObject getAsJSON(String data) {
    try {
      if (data == null) {
        return null;
      }
      return gson.fromJson(data, JsonObject.class);
    } catch (Exception e) {
      return null;
    }
  }

  public static void validateIsJson(String target, String json) {
    if (json == null) return;
    try {
      gson.fromJson(json, JsonObject.class);
    } catch (Exception e) {
      String errorMessage =
          String.format(
              "Not valid json: %s | Target: %s | User: %s",
              json, target, SecurityUtil.getCurrentUserName());
      logger.error(errorMessage, e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }
  }

  public static void toSiirtotiedostoJson(
      JSONParameter parameter, JsonWriter writer, SimpleDateFormat simpleDateFormat) {
    Date lastModified = parameter.getMuokattu();
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("key", parameter.getTarget());
    jsonObject.addProperty(
        "lastModified", lastModified != null ? simpleDateFormat.format(lastModified) : null);
    jsonObject.add("values", getAsJSON(parameter.getJsonValue()));
    gson.toJson(jsonObject, writer);
  }
}
