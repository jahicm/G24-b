package ch.g24.api.repository.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "MEDICATION")
public class MedicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICATION_ID")
    private Long medicationId;

    @Column(name = "MEDICATION_NAME")
    private String medicationName;

    @OneToMany(mappedBy = "medication")
    private List<UserEntity> users;

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

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }
}

