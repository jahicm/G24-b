package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DashBoardData {
    @JsonProperty("latest_readings")
    private LatestReadings latestReadings;

    @JsonProperty("weekly_overview")
    private WeeklyOverview weeklyOverview;

    private List<Medication> medications;

    @JsonProperty("ai_analysis")
    private AiAnalysis aiAnalysis;

    @JsonProperty("smart_insight")
    private SmartInsight smartInsight;

    // getters & setters
    public LatestReadings getLatestReadings() { return latestReadings; }
    public void setLatestReadings(LatestReadings latestReadings) { this.latestReadings = latestReadings; }

    public WeeklyOverview getWeeklyOverview() { return weeklyOverview; }
    public void setWeeklyOverview(WeeklyOverview weeklyOverview) { this.weeklyOverview = weeklyOverview; }

    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) { this.medications = medications; }

    public AiAnalysis getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(AiAnalysis aiAnalysis) { this.aiAnalysis = aiAnalysis; }

    public SmartInsight getSmartInsight() { return smartInsight; }
    public void setSmartInsight(SmartInsight smartInsight) { this.smartInsight = smartInsight; }
}

