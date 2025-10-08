package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<DataEntity, Long> {

    @Modifying
    @Query(value = "DELETE FROM public.data WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT d.data_id, d.user_id, d.data_entry_time, " +
            "d.measurement_entry_time, d.value, d.sugar_value, " +
            "d.reference_value, d.unit_id, d.status " +
            "FROM data d " +
            "INNER JOIN unit u ON d.unit_id = u.unit_id " +
            "WHERE d.user_id = :userId " +
            "AND d.measurement_entry_time >= NOW() - INTERVAL '3 months' " +
            "ORDER BY d.measurement_entry_time ASC",
            nativeQuery = true)
    List<DataEntity> getDataByUserId(@Param("userId") Long userId);
}

