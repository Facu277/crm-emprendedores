package com.emprendedores.crm.dto.seguridad;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private Long emprendedorId;
    private String role; // Rol principal del usuario

    public JwtResponse(String accessToken, Long id, String username, Long emprendedorId, String role) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.emprendedorId = emprendedorId;
        this.role = role;
    }
}
