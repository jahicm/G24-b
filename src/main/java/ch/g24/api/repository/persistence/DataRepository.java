package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<DataEntity, Long> {

    @Query(
            value = "SELECT  d.DATA_ID,d.USER_ID,d.DATA_ENTRY_TIME, d.MEASUREMENT_ENTRY_TIME, d.VALUE, d.SUGAR_VALUE, " +
                    "d.REFERENCE_VALUE, u.UNIT_NAME,d.UNIT_ID, d.STATUS " +
                    "FROM DATA d " +
                    "INNER JOIN UNIT u ON d.UNIT_ID = u.UNIT_ID " +
                    "WHERE d.USER_ID = :userId  AND d.MEASUREMENT_ENTRY_TIME >= DATEADD(MONTH, -3, GETDATE())",
            nativeQuery = true
    )
    List<DataEntity> getDataByUserId(@Param("userId") Long userId);
}

