package org.example.authJwt.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.IdGeneratorType;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordForgottenTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;
    private LocalDateTime expirationTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
