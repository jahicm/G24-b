package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysis {

    @JsonProperty("summary")
    private Summary summary;

    @JsonProperty("high_readings")
    private HighReadings highReadings;

    @JsonProperty("time_analysis")
    private TimeAnalysis timeAnalysis;

    @JsonProperty("recommendations")
    private List<String> recommendations;

    @JsonProperty("hba1c_prediction")
    private HbA1cPrediction hbA1cPrediction;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public HbA1cPrediction getHbA1cPrediction() {
        return hbA1cPrediction;
    }

    public void setHbA1cPrediction(HbA1cPrediction hbA1cPrediction) {
        this.hbA1cPrediction = hbA1cPrediction;
    }
}




