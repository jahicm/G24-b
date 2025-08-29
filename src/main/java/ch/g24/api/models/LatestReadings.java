package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatestReadings {
    private Reading reading;

    @JsonProperty("weekly_average")
    private WeeklyAverage weeklyAverage;

    public Reading getReading() {
        return reading;
    }

    public void setReading(Reading reading) {
        this.reading = reading;
    }

    public WeeklyAverage getWeeklyAverage() {
        return weeklyAverage;
    }

    public void setWeeklyAverage(WeeklyAverage weeklyAverage) {
        this.weeklyAverage = weeklyAverage;
    }
}

