package ch.g24.api.models;

import java.time.LocalDateTime;

public class Current {

    private double value;
    private String unit;
    private String status;
    private LocalDateTime timestamp;
    private String predictedHbA1c;

    // getters & setters
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPredictedHbA1c() {
        return predictedHbA1c;
    }

    public void setPredictedHbA1c(String predictedHbA1c) {
        this.predictedHbA1c = predictedHbA1c;
    }
}

