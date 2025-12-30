package com.emprendedores.crm.dto.categoria;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO mínimo para ser incrustado en otras entidades (Contenido).
 * Solo incluye la información de identificación.
 */
@Getter
@Setter
public class CategoriaMinResponseDTO {

    private Long id;
    private String nombre;
}
