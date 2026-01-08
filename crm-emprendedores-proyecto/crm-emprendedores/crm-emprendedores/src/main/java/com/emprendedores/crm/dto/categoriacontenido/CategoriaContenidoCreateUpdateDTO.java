package com.emprendedores.crm.dto.categoriacontenido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoriaContenidoCreateUpdateDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    
    private Long emprendedorId;
}
