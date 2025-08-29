package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Range {
    private String range;

    @JsonProperty("avg_value")
    private int avgValue;

    // getters & setters

}
