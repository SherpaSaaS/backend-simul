package org.example.authJwt.controllers;

import lombok.RequiredArgsConstructor;
import org.example.authJwt.dto.request.PasswordResetRequest;
import org.example.authJwt.models.PasswordForgottenTokens;
import org.example.authJwt.models.User;
import org.example.authJwt.repository.PasswordForgottenTokensRepository;
import org.example.authJwt.repository.UserRepository;
import org.example.authJwt.security.services.UserService;
import org.example.authJwt.services.PasswordForgottenTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@EnableDiscoveryClient
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordForgottenTokensRepository tokensRepository;

    @Autowired
    PasswordForgottenTokensService passwordForgottenTokensService;
    @PostMapping("/forgotPassword/{email}")
    public String forgotPassword(@PathVariable("email") String userEmail){
        String output = "";
        Optional<User> user =userRepository.findByEmail(userEmail);
        if(user.isPresent())
        {
          output=  passwordForgottenTokensService.sendEmail(user);

        }
        if (output.equals("success")) {
            return "email sent ";
        }
        return "user is not present";
    }
    @GetMapping("/reset-Password/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        PasswordForgottenTokens reset = tokensRepository.findByToken(token);
        if (reset != null && passwordForgottenTokensService.hasExipred(reset.getExpirationTime())) {
            model.addAttribute("email", reset.getUser().getEmail());
            return "resetPassword";
        }
        return "redirect:/forgotPassword?error";
    }
    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest,
                                                @PathVariable String token){
       passwordForgottenTokensService.validatePasswordResetToken(token);

       Optional<User> user = passwordForgottenTokensService.findUserByPasswordToken(token);

        if (user.isPresent()) {
            if (passwordForgottenTokensService.changePassword(user, passwordResetRequest.getNewPassword(), passwordResetRequest.getConfirmPassword())) {
                return ResponseEntity.ok("Password reset successful!");
            } else {
                return ResponseEntity.badRequest().body("Password not f token");
            }
        }
        return ResponseEntity.badRequest().body("Invalid password reset token");
    }
    /*
    @PostMapping("/change-password")
    public String changePassword(@RequestBody PasswordResetRequest request){
        User user = userRepository.findByEmail(request.getEmail()).get();
String email=request.getEmail();
        passwordForgottenTokensService.changePassword(Optional.of(user), request.getNewPassword()), request.getConfirmPassword());
        return "Password changed successfully";
    }
     */}



