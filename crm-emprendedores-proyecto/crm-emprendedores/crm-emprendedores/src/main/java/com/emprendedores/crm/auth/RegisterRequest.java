package com.emprendedores.crm.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro de nuevos usuarios en la plataforma.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    
    /** * ID del negocio al que pertenece este usuario. 
     * Fundamental para el aislamiento de datos. 
     */
    private Long emprendedorId; 
    
    private Long rolId; // Define el nivel de acceso (ADMIN, EMPRENDEDOR, EMPLEADO)
}
