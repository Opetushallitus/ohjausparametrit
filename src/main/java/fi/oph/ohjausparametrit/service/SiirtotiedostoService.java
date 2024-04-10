package fi.oph.ohjausparametrit.service;

import static fi.oph.ohjausparametrit.util.JsonUtil.getAsJSON;

import com.google.gson.JsonObject;
import fi.oph.ohjausparametrit.configurations.properties.SiirtotiedostoProperties;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.vm.sade.valinta.dokumenttipalvelu.SiirtotiedostoPalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import java.io.ByteArrayInputStream;
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

  public String createSiirtotiedosto(List<JSONParameter> data) {
    try {
      JsonObject jsonObject = new JsonObject();
      data.forEach(item -> jsonObject.add(item.getTarget(), getAsJSON(item.getJsonValue())));
      ObjectMetadata result =
          siirtotiedostoPalvelu.saveSiirtotiedosto(
              "ohjausparametrit",
              "parameter",
              "",
              new ByteArrayInputStream(jsonObject.toString().getBytes()),
              2);
      return result.key;
    } catch (Exception e) {
      logger.error("Failed to create siirtotiedosto; ", e);
      throw new RuntimeException(e);
    }
  }

  public int getMaxItemcountInTransferFile() {
    return siirtotiedostoProperties.getMaxItemcountInFile();
  }
}
