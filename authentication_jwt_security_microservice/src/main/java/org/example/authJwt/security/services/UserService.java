package org.example.authJwt.security.services;

import org.example.authJwt.models.User;
import org.example.authJwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@Service
public class UserService implements IUserService {

@Autowired
UserRepository userRepository;

    public UserDetails loadUserByUsername(String userEmail) {
       Optional<User> user =userRepository.findByEmail(userEmail);

        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    getAuthority(user.get())
            );
        } else {
            throw new UsernameNotFoundException("User not found with Email: " + userEmail);
        }
    }
    private Set getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getAuthorities().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority( role.getAuthority()));
        });
        return authorities;
    }

}
