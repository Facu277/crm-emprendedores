package com.emprendedores.crm.dto.user; // Ajusta a tu paquete de dtos

import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String nombre; 
    private String email;
    private String rol;
    private EmprendedorResponseDTO emprendedor;
}