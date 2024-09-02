package org.example.authJwt.services;

import org.example.authJwt.models.PasswordForgottenTokens;
import org.example.authJwt.models.User;
import org.example.authJwt.repository.PasswordForgottenTokensRepository;
import org.example.authJwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordForgottenTokensService {
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    PasswordForgottenTokensRepository passwordForgottenTokensRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /*public String generateForgottenToken(String userEmail){

        Date expirationDate = new Date(System.currentTimeMillis()*30+1000);
            Optional<User> user  = userRepository.findByEmail(userEmail);
            if(user.isPresent()){
                PasswordForgottenTokens token = new PasswordForgottenTokens(
                        null,
                        passwordEncoder.encode(user.get().getEmail()),
                        user.get()


                                        );
                return passwordForgottenTokensRepository.save(token).getToken();
            }else{
                throw new RuntimeException("user not found");
            }
    }*/

    public String sendEmail(Optional<User> user) {
        try {
        String link = generateResetToken(user);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("m.mahdouch1998@gmail.com");
        message.setTo(user.get().getEmail());
        message.setSubject("Forgotten password");
        message.setText("Hello,"
                + "You have requested to reset your password."
                + "Click the link below to change your password: "
                +  link + " Change my password"
                + "Ignore this email if you do remember your password, "
                + "or you have not made the request.");

        emailSender.send(message);
        return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

    private String generateResetToken(Optional<User> user) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
        PasswordForgottenTokens resetToken = new PasswordForgottenTokens();
        resetToken.setUser(user.get());
        resetToken.setToken(uuid.toString());
        resetToken.setExpirationTime(expiryDateTime);

        PasswordForgottenTokens token = passwordForgottenTokensRepository.save(resetToken);
        if (token != null) {
            String endpointUrl = "http://localhost:3000/resetPassword";
            return endpointUrl + "/" + resetToken.getToken();
        }
        return "";
    }

    public boolean hasExipred(LocalDateTime expiryDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isAfter(currentDateTime);
    }

    public ResponseEntity<String> validatePasswordResetToken(String passwordResetToken) {
        PasswordForgottenTokens passwordToken = passwordForgottenTokensRepository.findByToken(passwordResetToken);
            if(passwordToken == null){
                return ResponseEntity.badRequest().body("Invalid token");

            }

        LocalDateTime dateTimeNow = LocalDateTime.now(); // Current date and time



       if(passwordToken.getExpirationTime().isBefore(dateTimeNow))
       {
           return ResponseEntity.badRequest().body("Link already expired, resend link");
            }
        return ResponseEntity.ok().body("Valid Token");

    }

    public Optional<User> findUserByPasswordToken(String passwordResetToken) {
        return Optional.ofNullable(passwordForgottenTokensRepository.findByToken(passwordResetToken).getUser());
    }

    public boolean changePassword(Optional<User> user, String newPassword , String confirmPassword) {
      // if (  passwordEncoder.matches(newPassword, confirmPassword))
      if (newPassword.equals(confirmPassword)) {
           user.get().setPassword(passwordEncoder.encode(newPassword));
           userRepository.save(user.get());
           return true;
       }
       else {
           return false;
       }

    }
}
