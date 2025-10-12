package ch.g24.api.repository.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"USER\"")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private String surname;
    private LocalDate dob;
    private String diabetesType;
    private String userName;
    private String password;
    private LocalDateTime dataEntryTime;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({
            @JoinColumn(name = "post_code", referencedColumnName = "postCode"),
            @JoinColumn(name = "city", referencedColumnName = "city")
    })
    private LocationEntity location;
    private String unitId;
    @Column(name="medication_id")
    private Long medicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", insertable = false, updatable = false)
    private MedicationEntity medication;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getDiabetesType() {
        return diabetesType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitId() {
        return unitId;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDataEntryTime() {
        return dataEntryTime;
    }

    public void setDataEntryTime(LocalDateTime dataEntryTime) {
        this.dataEntryTime = dataEntryTime;
    }

    public MedicationEntity getMedication() {
        return medication;
    }

    public void setMedication(MedicationEntity medication) {
        this.medication = medication;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

}

