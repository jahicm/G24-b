package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestWorst {
    public String range;
    public double avg_value;

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public double getAvg_value() {
        return avg_value;
    }

    public void setAvg_value(double avg_value) {
        this.avg_value = avg_value;
    }
}
