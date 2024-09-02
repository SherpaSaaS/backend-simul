package org.example.authJwt.security.services;

import lombok.RequiredArgsConstructor;
import org.example.authJwt.models.User;
import org.example.authJwt.dto.reponse.AuthenticationResponse;
import org.example.authJwt.dto.request.AuthenticationRequest;
import org.example.authJwt.dto.request.RegisterRequest;
import org.example.authJwt.repository.UserRepository;
import org.example.authJwt.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthentifactionService {
    @Autowired
    UserRepository userRepository;



    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;

    public AuthenticationResponse register(User user) {
//TO DO :make sure that the email does not exist
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        // saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }


    public AuthenticationResponse authenticate(UserDetails userDetails) {

        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .email(userDetails.getUsername())
                .role(userDetails.getAuthorities().stream().toList().get(0).getAuthority())
                .build();
    }

    public String generateToken(UserDetails userDetails) {

        return jwtService.generateToken(userDetails);
    }

    public void validateToken(String token, UserDetails userDetails) {
        jwtService.isTokenValid(token, userDetails);
    }

    public void validateTokenn(String token) {
        jwtService.validateTokenn(token);
    }
}
