package ch.g24.api.repository.persistence;

import ch.g24.api.repository.entities.LocationId;
import ch.g24.api.repository.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    @Modifying
    @Query(value = "DELETE FROM public.user WHERE user_id = :userId", nativeQuery = true)
    void deleteUserByNativeId(@Param("userId") Long userId);
    Optional<UserEntity> findById(Long userId);
    Optional<UserEntity> findByUserName(String userName);
    boolean existsByLocation_LocationId(LocationId locationId);

}
