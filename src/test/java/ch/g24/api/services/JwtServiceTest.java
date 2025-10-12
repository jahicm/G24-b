package ch.g24.api.services;

import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserRepository userRepository;

    private JwtService jwtService;

    // ✅ 32-byte Base64 key (256 bits) for HS256
    private final String FAKE_SECRET = "dGhpcy1pcy1hLXZhbGlkLXNlY3JldC1rZXktMTIzNDU2";
    private final long EXPIRATION_MS = 3600000L; // 1 hour

    @BeforeEach
    void setup() {
        jwtService = new JwtService(FAKE_SECRET, EXPIRATION_MS, userRepository);
    }

    @Test
    void generateToken_shouldCreateValidJwt() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(123L);
        userEntity.setUserName("testuser");
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(userEntity));

        User userDetails = new User("testuser", "password", List.of());

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);

        boolean valid = jwtService.validateToken(token, userDetails);
        assertTrue(valid);
    }

    @Test
    void extractClaim_shouldReturnCorrectValue() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(456L);
        userEntity.setUserName("alice");
        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(userEntity));

        User userDetails = new User("alice", "pass", List.of());

        String token = jwtService.generateToken(userDetails);
        Long userId = jwtService.extractClaim(token, claims -> ((Number) claims.get("userId")).longValue());

        assertEquals(456L, userId);
    }

    @Test
    void validateToken_shouldFailForWrongUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(789L);
        userEntity.setUserName("bob");
        when(userRepository.findByUserName("bob")).thenReturn(Optional.of(userEntity));

        User userDetails = new User("bob", "pass", List.of());
        String token = jwtService.generateToken(userDetails);

        User otherUser = new User("alice", "pass", List.of());
        boolean valid = jwtService.validateToken(token, otherUser);

        assertFalse(valid);
    }
}
