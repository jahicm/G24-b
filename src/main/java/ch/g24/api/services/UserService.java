package ch.g24.api.services;

import ch.g24.api.enums.Medication;
import ch.g24.api.models.User;
import ch.g24.api.repository.entities.LocationEntity;
import ch.g24.api.repository.entities.LocationId;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.LocationRepository;
import ch.g24.api.repository.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UserService {


    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataRepository dataRepository;

    @Autowired
    public UserService(UserRepository userRepository, LocationRepository locationRepository, PasswordEncoder passwordEncoder, DataRepository dataRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
        this.dataRepository = dataRepository;
    }

    public User getUser(Long userId) {

        return userRepository.findById(userId).map(this::mapToUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    private User mapToUser(UserEntity entity) {
        return new User(entity.getUserId(), entity.getName(), entity.getSurname(), entity.getDob(),
                entity.getDiabetesType(), entity.getLocation().getLocationId().getPostCode(), entity.getLocation().getLocationId().getCity(),
                entity.getLocation().getCountry(), entity.getUnitId(), entity.getUserName(), entity.getMedication().getMedicationName(), entity.getPassword(), entity.getDataEntryTime());
    }

    @Transactional
    public boolean saveUser(User user) {
        try {

            UserEntity existingUser = userRepository.findByUserName(user.email())
                    .orElse(null);

            UserEntity userEntity;
            if (existingUser != null) {
                // Update existing user
                userEntity = existingUser;

            } else {
                // Create new user
                userEntity = new UserEntity();
                userEntity.setUserName(user.email());
                userEntity.setPassword(passwordEncoder.encode(user.password()));
            }
            userEntity.setMedicationId(Medication.getMedicationIdByName(user.medication()));
            userEntity.setName(user.name());
            userEntity.setSurname(user.lastName());
            userEntity.setDiabetesType(user.diabetesType());
            userEntity.setDob(user.dob());
            userEntity.setUnitId(String.valueOf(user.unit()));
            userEntity.setDataEntryTime(LocalDateTime.now());
            LocationId locationId = new LocationId();
            locationId.setPostCode(user.postCode());
            locationId.setCity(user.city());
            LocationEntity locationToUse = locationRepository.findById(locationId)
                    .orElseGet(() -> {
                        LocationEntity newLocation = new LocationEntity();
                        newLocation.setLocationId(locationId);
                        newLocation.setCountry(user.country());
                        return locationRepository.save(newLocation);
                    });

            userEntity.setLocation(locationToUse);

            // Save or update the user
            userRepository.save(userEntity);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Log full stack trace for debugging
            System.err.println("Fehler beim Speichern des Benutzers: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean deleteProfile(String userId) {
        try {
            Long id = Long.parseLong(userId);

            // 1. Fetch the user
            Optional<UserEntity> userEntityOpt = userRepository.findById(id);
            if (userEntityOpt.isEmpty()) {
                System.err.println("⚠️ User not found: " + userId);
                return false;
            }

            UserEntity user = userEntityOpt.get();
            LocationEntity location = user.getLocation();

            // 2. Delete user’s data first
            try {
                dataRepository.deleteAllByUserId(id);
            } catch (Exception e) {
                System.err.println("❌ Failed to delete data entries for user " + userId + ": " + e.getMessage());
                e.printStackTrace();
                return false; // rollback transaction
            }

            // 3. Delete user
            try {
                userRepository.deleteUserByNativeId(id);
            } catch (Exception e) {
                System.err.println("❌ Failed to delete user " + userId + ": " + e.getMessage());
                e.printStackTrace();
                return false; // rollback transaction
            }

            // 4. Optionally check if location can be deleted
            if (location != null) {
                try {
                    boolean isLocationUsed = userRepository.existsByLocation_LocationId(location.getLocationId());
                    if (!isLocationUsed) {
                        locationRepository.deleteLocation(location.getLocationId().getPostCode(),location.getLocationId().getCity());
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to delete or check location for user " + userId + ": " + e.getMessage());
                    e.printStackTrace();
                    // don't fail the transaction — not critical
                }
            }
            return true;

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid user ID format: " + userId);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while deleting user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
