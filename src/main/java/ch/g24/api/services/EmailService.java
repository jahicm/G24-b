package ch.g24.api.services;

import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {


    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private Optional<UserEntity> user;


    public EmailService(JavaMailSender mailSender, UserRepository userRepository, JwtService jwtService) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public void sendWelcomeEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@myapp.com");
        message.setTo(to);
        message.setSubject("Welcome to MyApp 🎉");
        message.setText("Hello " + name + ",\n\nWelcome to MyApp! We're glad to have you 🚀.");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@myapp.com");
        message.setTo(to);
        message.setSubject("G24 password reset");
        String resetToken = jwtService.generateResetToken(to,30);
        System.out.println("Token:"+resetToken);
        String resetLink = "http://localhost:4200/g24/reset-password?token=" + resetToken;
        message.setText("Username:"+to+"\n"+"Your password URL:\n" + resetLink);
        mailSender.send(message);
    }
}
