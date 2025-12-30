package com.emprendedores.crm.dto.rol;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RolCreateUpdateDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombre; // Ej: ADMIN, USER
}
