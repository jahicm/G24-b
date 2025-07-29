package ch.g24.api.repository.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "[USER]")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    private String name;
    private String surname;
    private LocalDate dob;
    private String diabetesType;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({
            @JoinColumn(name = "post_code", referencedColumnName = "postCode"),
            @JoinColumn(name = "city", referencedColumnName = "city")
    })
    private LocationEntity location;
    private String medication;
    private String unit;

    public String getUserId() {
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

    public String getMedication() {
        return medication;
    }

    public String getUnit() {
        return unit;
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

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    public LocationEntity getLocation() {
        return location;
    }
    public void setLocation(LocationEntity location) {
        this.location = location;
    }
}

