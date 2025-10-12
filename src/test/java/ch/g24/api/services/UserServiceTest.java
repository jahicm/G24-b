package ch.g24.api.services;

import ch.g24.api.enums.Medication;
import ch.g24.api.models.User;
import ch.g24.api.repository.entities.LocationEntity;
import ch.g24.api.repository.entities.LocationId;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.LocationRepository;
import ch.g24.api.repository.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User(
                1L,
                "John",
                "Doe",
                LocalDate.of(1990,1,1),
                "Type1",
                "1000",
                "Zürich",
                "Switzerland",
                "mmol/L",
                "john@example.com",
                Medication.MEDICATION_INSULIN.getMedicationName(),
                "password123",
                LocalDateTime.now()
        );
    }

    @Test
    void saveUser_createsNewUser() {
        // Arrange
        when(userRepository.findByUserName(testUser.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUser.password())).thenReturn("encodedPassword");
        when(locationRepository.findById(any(LocationId.class))).thenReturn(Optional.empty());
        when(locationRepository.save(any(LocationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = userService.saveUser(testUser);

        // Assert
        assertTrue(result);
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void saveUser_updatesExistingUser() {
        // Arrange
        UserEntity existing = new UserEntity();
        existing.setUserName(testUser.email());
        when(userRepository.findByUserName(testUser.email())).thenReturn(Optional.of(existing));
        when(locationRepository.findById(any(LocationId.class))).thenReturn(Optional.empty());
        when(locationRepository.save(any(LocationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = userService.saveUser(testUser);

        // Assert
        assertTrue(result);
        verify(userRepository).save(existing);
        verify(passwordEncoder, never()).encode(anyString()); // Password not re-encoded
    }

    @Test
    void deleteProfile_successfulDeletion() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        LocationId locId = new LocationId();
        locId.setCity("Zürich");
        locId.setPostCode("8004");
        LocationEntity location = new LocationEntity();
        location.setLocationId(locId);
        userEntity.setLocation(location);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByLocation_LocationId(locId)).thenReturn(false);

        // Act
        boolean result = userService.deleteProfile("1");

        // Assert
        assertTrue(result);
        verify(dataRepository).deleteAllByUserId(1L);
        verify(userRepository).deleteUserByNativeId(1L);
        verify(locationRepository).deleteLocation("8004", "Zürich");
    }

    @Test
    void deleteProfile_userNotFound_returnsFalse() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = userService.deleteProfile("999");

        assertFalse(result);
        verify(dataRepository, never()).deleteAllByUserId(anyLong());
        verify(userRepository, never()).deleteUserByNativeId(anyLong());
    }

    @Test
    void deleteProfile_invalidIdFormat_returnsFalse() {
        boolean result = userService.deleteProfile("invalid");

        assertFalse(result);
        verify(dataRepository, never()).deleteAllByUserId(anyLong());
        verify(userRepository, never()).deleteUserByNativeId(anyLong());
    }
}
