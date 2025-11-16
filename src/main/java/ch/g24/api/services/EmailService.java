package ch.g24.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender mailSender;
    private final JwtService jwtService;
    private final String springEmailFrom;

    public EmailService(JavaMailSender mailSender, JwtService jwtService, @Value("${spring.mail.from}") String springEmailFrom) {
        this.mailSender = mailSender;
        this.jwtService = jwtService;
        this.springEmailFrom = springEmailFrom;
    }
    public void sendWelcomeEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(springEmailFrom);
        message.setTo(to);
        message.setSubject("Welcome to G24 🎉,Willkommen bei G24 🎉,Bienvenue sur G24 🎉,Benvenuto su G24 🎉");
        message.setText("Hello " + to + ",\n\nWelcome to G24! We're glad to have you 🚀.\n"+
        "Hallo " + to + ",\n\nWillkommen bei G24! Wir freuen uns, dass du dabei bist 🚀.\n"+
        "Bonjour " + to + ",\n\nBienvenue sur G24 ! Nous sommes ravis de vous accueillir 🚀.\n"+
        "Ciao " + to + ",\n\nBenvenuto su G24! Siamo felici di averti con noi 🚀.");

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(springEmailFrom);
        message.setTo(to);
        message.setSubject("G24 password reset");
        String resetToken = jwtService.generateResetToken(to);

        String resetLink = "https://www.g-24.ch/g24/reset-password?token=" + resetToken;
        message.setText("Username:"+to+"\n"+"Password URL:\n" + resetLink+"\n"+" Please use this link to reset your password within 1 hour.\n"
        +"\" Bitte verwenden Sie diesen Link, um Ihr Passwort innerhalb von 1 Stunde zurückzusetzen.\n"
        +"\" Veuillez utiliser ce lien pour réinitialiser votre mot de passe dans l'heure qui suit.\n"
        +"\" \"Usa questo link per reimpostare la password entro 1 ora.\n");

        mailSender.send(message);
    }
}
