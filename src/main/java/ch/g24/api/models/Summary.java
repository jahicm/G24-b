package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {
    public double weekly_avg;
    public String unit;
    public String trend;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public double getWeekly_avg() {
        return weekly_avg;
    }

    public void setWeekly_avg(double weekly_avg) {
        this.weekly_avg = weekly_avg;
    }
}
