package com.emprendedores.crm.dto.categoriacontenido;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class CategoriaContenidoMinResponseDTO {

    private Long id;
    private String nombre;
}