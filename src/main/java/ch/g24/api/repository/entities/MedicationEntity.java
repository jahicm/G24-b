package ch.g24.api.repository.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "MEDICATION")
public class MedicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICATION_ID")
    private Long medicationId;

    @Column(name = "MEDICATION_NAME")
    private String medicationName;

    @OneToOne(mappedBy = "medication")
    private UserEntity user;

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

}

