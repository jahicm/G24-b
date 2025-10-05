package ch.g24.api.services;

import ch.g24.api.enums.Medication;
import ch.g24.api.enums.SugarUnit;
import ch.g24.api.models.User;
import ch.g24.api.repository.entities.LocationEntity;
import ch.g24.api.repository.entities.LocationId;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.LocationRepository;
import ch.g24.api.repository.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class UserService {


    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, LocationRepository localtionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.locationRepository = localtionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(Long userId) {

        return userRepository.findById(userId).map(this::mapToUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    private User mapToUser(UserEntity entity) {
        return new User(entity.getUserId(),entity.getName(), entity.getSurname(), entity.getDob(),
                entity.getDiabetesType(), entity.getLocation().getLocationId().getPostCode(), entity.getLocation().getLocationId().getCity(),
                entity.getLocation().getCountry(), SugarUnit.getLabelById(Integer.parseInt(entity.getUnitId())), entity.getUserName(),entity.getMedication().getMedicationName(), entity.getPassword(),entity.getDataEntryTime());
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
            userEntity.setUnitId(String.valueOf(SugarUnit.getIdByLabel(user.unit())));
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
}
