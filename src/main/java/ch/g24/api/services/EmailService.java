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

    public void sendWelcomeEmail(String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@myapp.com");
        message.setTo(name);
        message.setSubject("Welcome to 24 🎉");
        message.setText("Hello " + name + ",\n\nWelcome to G24! We're glad to have you 🚀.\n"+
        "Hallo " + name + ",\n\nWillkommen bei G24! Wir freuen uns, dass du dabei bist 🚀.\n"+
        "Bonjour " + name + ",\n\nBienvenue sur G24 ! Nous sommes ravis de vous accueillir 🚀.\n"+
        "Ciao " + name + ",\n\nBenvenuto su G24! Siamo felici di averti con noi 🚀.");


        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@myapp.com");
        message.setTo(to);
        message.setSubject("G24 password reset");
        String resetToken = jwtService.generateResetToken(to);
        System.out.println("Token:"+resetToken);
        String resetLink = "http://localhost:4200/g24/reset-password?token=" + resetToken;
        message.setText("Username:"+to+"\n"+"Password URL:\n" + resetLink+"\n"+" Please use this link to reset your password within 1 hour.\n"
        +"\" Bitte verwenden Sie diesen Link, um Ihr Passwort innerhalb von 1 Stunde zurückzusetzen.\n"
        +"\" Veuillez utiliser ce lien pour réinitialiser votre mot de passe dans l'heure qui suit.\n"
        +"\" \"Usa questo link per reimpostare la password entro 1 ora.\n");

        mailSender.send(message);
    }
}
