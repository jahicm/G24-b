package ch.g24.api.repository.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "MEDICATION")
public class MedicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICATION_ID")
    private Long medicationId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "MEDICATION_NAME")
    private String medicationName;

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }
}

