package com.emprendedores.crm.dto.rol;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class RolResponseDTO {

    private Long id;
    private String nombre;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
