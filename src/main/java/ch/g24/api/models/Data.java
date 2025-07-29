package ch.g24.api.models;

import java.time.LocalDateTime;

public class Data {

    private Long dataId;
    private String userId;
    private LocalDateTime dataEntryTime;
    private LocalDateTime measurementTime;
    private String value;
    private double sugarValue;
    private String referenceValue;
    private String unit;
    private String status;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }
   public LocalDateTime getDataEntryTime() {
        return dataEntryTime;
    }

    public void setDataEntryTime(LocalDateTime dataEntryTime) {
        this.dataEntryTime = dataEntryTime;
    }

    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(LocalDateTime measurementTime) {
        this.measurementTime = measurementTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getSugarValue() {
        return sugarValue;
    }

    public void setSugarValue(double sugarValue) {
        this.sugarValue = sugarValue;
    }

    public String getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(String referenceValue) {
        this.referenceValue = referenceValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
