package ch.g24.api.models;

import java.time.LocalDateTime;

public class Reading {

    private String date;
    private Double sugarValue;
    private String unit;
    private String context;

    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }

    // getters & setters
    public  String getDate() {
        return date;
    }

    public void setDate( String date) {
        this.date = date;
    }

    public Double getSugarValue() {
        return sugarValue;
    }

    public void setSugarValue(Double sugarValue) {
        this.sugarValue = sugarValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
