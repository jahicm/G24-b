package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<MedicationEntity,Long> {

    @Query(value = "SELECT m.MEDICATION_ID,m.USER_ID,m.MEDICATION_NAME FROM MEDICATION m WHERE m.USER_ID=:userId", nativeQuery = true)
    List<MedicationEntity> findAllMedicationsByUserId(@Param("userId") Long userId);
}
