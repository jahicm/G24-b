package ch.g24.api.services;

import ch.g24.api.models.User;
import ch.g24.api.repository.entities.LocationEntity;
import ch.g24.api.repository.entities.LocationId;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.LocationRepository;
import ch.g24.api.repository.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {


    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public UserService(UserRepository userRepository, LocationRepository localtionRepository) {
        this.userRepository = userRepository;
        this.locationRepository = localtionRepository;
    }

    public User getUser(Long userId) {

        return userRepository.findById(userId).map(this::mapToUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    private User mapToUser(UserEntity entity) {
        return new User(entity.getUserId(), entity.getName(), entity.getSurname(), entity.getDob(),
                entity.getDiabetesType(), entity.getLocation().getLocationId().getPostCode(), entity.getLocation().getLocationId().getCity(),
                entity.getLocation().getCountry(), entity.getMedication(), entity.getUnit());
    }

    @Transactional
    public boolean saveUser(User user) {
        try {

            UserEntity existingUser = userRepository.findById(Long.valueOf(user.userId()))
                    .orElse(null);

            UserEntity userEntity;
            if (existingUser != null) {
                // Update existing user
                userEntity = existingUser;
            } else {
                // Create new user
                userEntity = new UserEntity();
            }

            // Update user fields
            userEntity.setName(user.name());
            userEntity.setSurname(user.lastName());
            userEntity.setDiabetesType(user.diabetesType());
            userEntity.setDob(user.dob());
            userEntity.setMedication(user.medication());
            userEntity.setUnit(user.unit());

            // Handle location
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
