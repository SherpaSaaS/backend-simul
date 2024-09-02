package org.example.authJwt.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.authJwt.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class    AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain //list of the other filter that we need to excute
    ) throws ServletException, IOException {
        /*
        to check the jwt token
         */
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
          if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
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
            UserDetails userDetails = userService.loadUserByUsername(userEmail);

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
        }
        }
        // pass the hand to the next filter to be excuted
        filterChain.doFilter(request,response);
    }
}
