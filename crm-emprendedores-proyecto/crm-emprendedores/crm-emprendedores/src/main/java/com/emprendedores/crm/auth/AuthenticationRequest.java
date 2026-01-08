package com.emprendedores.crm.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para capturar las credenciales durante la solicitud de inicio de sesión.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String username; // Generalmente el email del emprendedor
    private String password; // Contraseña en texto plano (se cifrará para comparar)
}
