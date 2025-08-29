package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class GlucoseData {

    public String patient_id;
    public List<GlucoseReading> glucose_readings;



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GlucoseReading {
        public String timestamp;
        public double value;
        public String unit;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

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
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public List<GlucoseReading> getGlucose_readings() {
        return glucose_readings;
    }

    public void setGlucose_readings(List<GlucoseReading> glucose_readings) {
        this.glucose_readings = glucose_readings;
    }
}

