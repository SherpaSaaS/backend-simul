package org.example.authJwt.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.authJwt.models.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTokenResponse {
    private String message;
    private Boolean isValid;
    private String role;
}
