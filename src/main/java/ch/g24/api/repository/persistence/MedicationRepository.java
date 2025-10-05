package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<MedicationEntity,Long> {

    @Query(value = "SELECT m.medication_id,m.medication_name,u.user_id FROM public.MEDICATION m INNER JOIN public.user u ON m.medication_id=u.medication_id WHERE u.user_id=:userId", nativeQuery = true)
    Optional<MedicationEntity> findAllMedicationsByUserId(@Param("userId") Long userId);
}
