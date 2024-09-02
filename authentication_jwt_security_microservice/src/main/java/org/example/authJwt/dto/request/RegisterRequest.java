package org.example.authJwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.authJwt.models.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private Role role;
}
