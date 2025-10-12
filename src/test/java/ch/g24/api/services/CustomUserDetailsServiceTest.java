package ch.g24.api.services;

import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        UserEntity mockUser = new UserEntity();
        mockUser.setUserName("mirza");
        mockUser.setPassword("securePassword");

        when(userRepository.findByUserName("mirza")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("mirza");

        // Assert
        assertNotNull(userDetails);
        assertEquals("mirza", userDetails.getUsername());
        assertEquals("securePassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findByUserName("mirza");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent"));

        verify(userRepository, times(1)).findByUserName("nonexistent");
    }
}
