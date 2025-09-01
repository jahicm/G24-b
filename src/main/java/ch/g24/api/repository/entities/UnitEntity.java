package ch.g24.api.repository.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "UNIT")
public class UnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long unitId;

    @Column(name="UNIT_NAME")
    private String unitName;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
