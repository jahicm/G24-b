package ch.g24.api.repository.entities;

import ch.g24.api.repository.entities.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DATA")
public class DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ID")
    private Long dataId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Column(name = "DATA_ENTRY_TIME")
    private LocalDateTime dataEntryTime;

    @Column(name = "MEASUREMENT_ENTRY_TIME")
    private LocalDateTime measurementEntryTime;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "SUGAR_VALUE")
    private double sugarValue;

    @Column(name = "REFERENCE_VALUE")
    private String referenceValue;

    @ManyToOne
    @JoinColumn(name = "UNIT_ID")
    private UnitEntity unitEntity;

    @Column(name = "STATUS")
    private String status;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getDataEntryTime() {
        return dataEntryTime;
    }

    public void setDataEntryTime(LocalDateTime dataEntryTime) {
        this.dataEntryTime = dataEntryTime;
    }

    public LocalDateTime getMeasurementEntryTime() {
        return measurementEntryTime;
    }

    public void setMeasurementEntryTime(LocalDateTime measurementEntryTime) {
        this.measurementEntryTime = measurementEntryTime;
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

    public UnitEntity getUnit() {
        return unitEntity;
    }

    public void setUnit(UnitEntity unit) {
        this.unitEntity = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
