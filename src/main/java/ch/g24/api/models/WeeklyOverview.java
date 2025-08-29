package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WeeklyOverview {
    private List<Reading> readings;

    @JsonProperty("time_range")
    private TimeRange timeRange;

    // getters & setters
    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }
}

