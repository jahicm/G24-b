package ch.g24.api.controller;

import ch.g24.api.models.ResetPasswordRequest;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.UserRepository;
import ch.g24.api.security.AuthRequest;
import ch.g24.api.services.EmailService;
import ch.g24.api.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginController(AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {

        UserEntity userEntity = userRepository.findByUserName(email).orElseThrow(() -> new RuntimeException("Failed to find user"));
        emailService.sendPasswordResetEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset URL sent.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ResetPasswordRequest request) {

        try {
            String newPassword = request.getPassword();
            String email = jwtService.extractUsername(request.getToken());
            UserEntity userEntity = userRepository.findByUserName(email).orElseThrow(() -> new RuntimeException("User not retrieved for the password reset"));
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userEntity);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token ❌"));
        }

    }

}
