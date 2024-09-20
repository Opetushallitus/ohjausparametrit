package fi.oph.ohjausparametrit.ovara.ajastus;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "SIIRTOTIEDOSTO", schema = "PUBLIC")
public class SiirtotiedostoProsessi {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return String.format("Window %s - %s, success %s", windowStart, windowEnd, success);
    }
}
