package com.emprendedores.crm.dto.emprendedor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter @Setter
public class EmprendedorMinResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String rubro;

    // Campo compuesto para mayor comodidad en el frontend
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
