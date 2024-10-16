package fi.oph.ohjausparametrit.service.ovara;

import com.google.gson.stream.JsonWriter;
import fi.oph.ohjausparametrit.configurations.SiirtotiedostoConstants;
import fi.oph.ohjausparametrit.configurations.properties.SiirtotiedostoProperties;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.util.JsonUtil;
import fi.vm.sade.valinta.dokumenttipalvelu.SiirtotiedostoPalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiirtotiedostoService {
  private static final Logger logger = LoggerFactory.getLogger(SiirtotiedostoService.class);

  private final SiirtotiedostoProperties siirtotiedostoProperties;
  private final SiirtotiedostoPalvelu siirtotiedostoPalvelu;

  public SiirtotiedostoService(SiirtotiedostoProperties siirtotiedostoProperties) {
    this.siirtotiedostoProperties = siirtotiedostoProperties;
    siirtotiedostoPalvelu =
        new SiirtotiedostoPalvelu(
            siirtotiedostoProperties.getAwsRegion(),
            siirtotiedostoProperties.getS3BucketName(),
            siirtotiedostoProperties.getS3bucketTargetRoleArn());
  }

  public String createSiirtotiedosto(
      List<JSONParameter> data, String operationId, int operationSubId) {
    SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat(SiirtotiedostoConstants.SIIRTOTIEDOSTO_DATETIME_FORMAT);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
        JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);
        jsonWriter.beginArray();
        for (JSONParameter parameter : data) {
          JsonUtil.toSiirtotiedostoJson(parameter, jsonWriter, simpleDateFormat);
        }
        jsonWriter.endArray();
        jsonWriter.close();

        try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(outputStream.toByteArray())) {
          return doCreateSiirtotiedosto(inputStream, operationId, operationSubId);
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException("JSONin muodostaminen epäonnistui;", ioe);
    }
  }

  private String doCreateSiirtotiedosto(InputStream inputStream, String opId, int opSubId) {
    try {
      ObjectMetadata result =
          siirtotiedostoPalvelu.saveSiirtotiedosto(
              "ohjausparametrit", "parameter", "", opId, opSubId, inputStream, 2);
      return result.key;
    } catch (Exception e) {
      logger.error("Siirtotiedoston luonti epäonnistui; ", e);
      throw new RuntimeException(e);
    }
  }

  public int getMaxItemcountInTransferFile() {
    return siirtotiedostoProperties.getMaxItemcountInFile();
  }
}
