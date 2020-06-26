package io.pivotal.sfdc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SFDC Health Status Object representation
 * 
 * @author Jignesh Sheth
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SFDCStatus {

    @JsonProperty(value = "status")
    String status;

    @JsonProperty(value = "isActive")
    Boolean isActive;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
