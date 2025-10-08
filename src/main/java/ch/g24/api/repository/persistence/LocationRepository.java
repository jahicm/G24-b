package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.LocationEntity;
import ch.g24.api.repository.entities.LocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, LocationId> {
    @Modifying
    @Query(value = "DELETE FROM public.location WHERE post_code = :postCode AND city= :city", nativeQuery = true)
    void deleteLocation(@Param("postCode") String postCode, @Param("city") String city);
}
