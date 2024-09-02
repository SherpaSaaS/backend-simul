package org.example.authJwt.config;

import lombok.RequiredArgsConstructor;
import org.example.authJwt.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.example.authJwt.models.Role.ADMIN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


   // private final LogoutHandler logoutHandler;
@Autowired
AuthenticationProvider authenticationProvider;

    @Autowired
    AuthTokenFilter jwtAuthFilter;

    private static final String[] WHITE_LIST_URL = {
            "/api/auth/authenticate",
            "/api/auth/register",
            "/api/auth/validate",
            "/api/auth/validatee",
            "/api/auth/forgotPassword/**",
            "/api/auth/resetPassword/**"

           };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // whitlist all the list of request matchers : application pattern
                //permit all : authorized all the request
                // but any other request should authenticated first
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/fmu/**").hasAnyRole(ADMIN.name())

                                .requestMatchers( " /api/bloc/**").hasAnyRole("ADMIN")
                                .requestMatchers( " /api/variables/**").hasAnyRole("ADMIN")
                                .requestMatchers( "/api/simulation/**").hasAnyRole("USER")
                                .anyRequest()
                                .authenticated()
                )
                // session create policy : how i want the  create the session => stateless
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                //which authentication provider we want to use :
                .authenticationProvider(authenticationProvider)
                // use the jwt filter that we've created
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/logout")

                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }
}
