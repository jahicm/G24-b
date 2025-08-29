package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {
    public double weekly_avg;
    public String unit;
    public String trend;
}
