package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.LocationEntity;
import ch.g24.api.repository.entities.LocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, LocationId> {
}
