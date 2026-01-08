package com.emprendedores.crm.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que transporta los tokens generados tras una autenticación exitosa.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    
    @JsonProperty("access_token")
    private String accessToken; // Token de corta duración para peticiones API
    
    @JsonProperty("refresh_token")
    private String refreshToken; // Token de larga duración para renovar el access_token
}
