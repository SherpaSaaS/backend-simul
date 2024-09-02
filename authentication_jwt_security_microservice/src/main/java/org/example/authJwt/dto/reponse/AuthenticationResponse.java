package org.example.authJwt.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor

public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh-token")
    private String refreshToken;
    @JsonProperty("email")
    private String email;
    @JsonProperty("role")
    private String role;

}
