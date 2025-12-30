package com.emprendedores.crm.dto.contenido;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ContenidoMinResponseDTO {

    private Long id;
    private String titulo;
    private String estado;
    private LocalDateTime fechaProgramada;
}
