package org.example.authJwt.repository;

import org.example.authJwt.models.PasswordForgottenTokens;
import org.example.authJwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordForgottenTokensRepository extends JpaRepository<PasswordForgottenTokens, Integer> {
    PasswordForgottenTokens findByToken(String token);
}
