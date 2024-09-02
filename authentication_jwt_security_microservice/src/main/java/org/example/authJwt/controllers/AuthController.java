package org.example.authJwt.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.authJwt.dto.reponse.AuthenticationResponse;
import org.example.authJwt.dto.reponse.ValidateTokenResponse;
import org.example.authJwt.dto.request.AuthenticationRequest;
import org.example.authJwt.dto.request.RegisterRequest;
import org.example.authJwt.models.Role;
import org.example.authJwt.models.User;
import org.example.authJwt.security.jwt.JwtService;
import org.example.authJwt.security.services.AuthentifactionService;
import org.example.authJwt.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@EnableDiscoveryClient

public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthentifactionService authentifactionService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;


    private final UserDetailsService userDetailsService;


    @Autowired
    JwtService jwtService;



    /*
    Register logic
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
  User user = new User(null,
          request.getFirst_name(),
          request.getLast_name(),
          request.getEmail(),
          passwordEncoder.encode(request.getPassword()),
          request.getRole(),
          false);
        return ResponseEntity.ok(authentifactionService.register(user));
    }

    /*
    Authentification logic
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()

                )
        );

        final UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
        return ResponseEntity.ok(authentifactionService.authenticate(userDetails));
    }


    /*
    Token logic : to get the token when a request is sent
     */
    @PostMapping("/token")
    public String getToken(@RequestBody AuthenticationRequest authRequest) {

        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(authRequest.getEmail());
        } catch (UsernameNotFoundException e) {
            return "Invalid username or password";
        }

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return authentifactionService.generateToken(userDetails);
            // You can optionally generate a refresh token here and include it in the response
        } else {
            throw new RuntimeException("invalid access");

        }
    }




    @GetMapping("/validatee")
    public ValidateTokenResponse validateToken(HttpServletRequest request, @RequestParam("token") String token) {

/*
        to check the jwt token
         */
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        UserDetails userDetails;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return null;
        }
        jwt = authHeader.substring(7);

        /*
        call the UserDetailService to check if we have the user in our db or not :
        1- extract the userEmail from jwt token from JwtService
         */
        userEmail = jwtService.extractUsername(jwt);
        // if we have ou user email and the user is not auth
        // we get the userDetail form the DB
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // then we check if the user is valid or not : ok : we create an object of username auth token

            if (jwtService.isTokenValid(jwt,userDetails) ) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // extend this auth token with the details of our request and then update
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                return new ValidateTokenResponse("Token is valid",
                true,
                userDetails.getAuthorities().stream().toList().get(0).getAuthority());
            }
        }
        else if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
            userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            return new ValidateTokenResponse("Token is valid",
                    true,
                    userDetails.getAuthorities().stream().toList().get(0).getAuthority());
        }else{
            return null;
        }
        return null;
    }




}
