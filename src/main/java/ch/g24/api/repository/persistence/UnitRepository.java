package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UnitRepository extends JpaRepository<UnitEntity,Long> {
}
