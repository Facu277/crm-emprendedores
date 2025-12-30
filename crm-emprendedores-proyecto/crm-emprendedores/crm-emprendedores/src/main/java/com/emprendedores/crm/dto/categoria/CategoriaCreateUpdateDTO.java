package com.emprendedores.crm.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la entrada de datos (Creación y Actualización).
 * Contiene solo los campos modificables por el cliente (sin el ID).
 */
@Getter @Setter
public class CategoriaCreateUpdateDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;
}
