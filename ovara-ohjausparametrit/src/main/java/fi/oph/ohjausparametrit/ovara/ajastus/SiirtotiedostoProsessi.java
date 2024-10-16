package fi.oph.ohjausparametrit.ovara.ajastus;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "SIIRTOTIEDOSTO", schema = "PUBLIC")
public class SiirtotiedostoProsessi {
  @Id
  @Column(name = "execution_uuid")
  private String executionUuid;

  @Column(name = "window_start", nullable = false)
  private OffsetDateTime windowStart;

  @Column(name = "window_end", nullable = false)
  private OffsetDateTime windowEnd;

  @Column(name = "run_start", nullable = false)
  private OffsetDateTime runStart;

  @Column(name = "run_end")
  private OffsetDateTime runEnd;

  @Column(name = "info")
  @Type(type = "com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType")
  private JsonNode info;

  @Column(name = "success")
  private Boolean success;

  @Column(name = "error_message")
  private String errorMessage;

  public SiirtotiedostoProsessi() {}
  ;

  public SiirtotiedostoProsessi(
      String executionUuid,
      OffsetDateTime windowStart,
      OffsetDateTime windodwEnd,
      OffsetDateTime runStart,
      OffsetDateTime runEnd,
      JsonNode info,
      Boolean success,
      String errorMessage) {
    this.executionUuid = executionUuid;
    this.windowStart = windowStart;
    this.windowEnd = windodwEnd;
    this.runStart = runStart;
    this.runEnd = runEnd;
    this.info = info;
    this.success = success;
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public JsonNode getInfo() {
    return info;
  }

  public void setInfo(JsonNode info) {
    this.info = info;
  }

  public OffsetDateTime getWindowEnd() {
    return windowEnd;
  }

  public void setWindowEnd(OffsetDateTime windowEnd) {
    this.windowEnd = windowEnd;
  }

  public OffsetDateTime getWindowStart() {
    return windowStart;
  }

  public void setWindowStart(OffsetDateTime windowStart) {
    this.windowStart = windowStart;
  }

  public String getExecutionUuid() {
    return executionUuid;
  }

  public void setExecutionUuid(String executionUuid) {
    this.executionUuid = executionUuid;
  }

  public OffsetDateTime getRunEnd() {
    return runEnd;
  }

  public void setRunEnd(OffsetDateTime runEnd) {
    this.runEnd = runEnd;
  }

  public OffsetDateTime getRunStart() {
    return runStart;
  }

  public void setRunStart(OffsetDateTime runStart) {
    this.runStart = runStart;
  }

  public SiirtotiedostoProsessi createNewProcessBasedOnThis() {
    return new SiirtotiedostoProsessi(
        UUID.randomUUID().toString(),
        this.windowEnd,
        OffsetDateTime.now(),
        OffsetDateTime.now(),
        null,
        null,
        false,
        "");
  }

  @Override
  public String toString() {
    return "SiirtotiedostoProsessi{"
        + "executionUuid="
        + executionUuid
        + ", windowStart="
        + windowStart
        + ", windowEnd="
        + windowEnd
        + ", runStart="
        + runStart
        + ", runEnd="
        + runEnd
        + ", info="
        + info
        + ", success="
        + success
        + ", errorMessage='"
        + errorMessage
        + '\''
        + '}';
  }
}
