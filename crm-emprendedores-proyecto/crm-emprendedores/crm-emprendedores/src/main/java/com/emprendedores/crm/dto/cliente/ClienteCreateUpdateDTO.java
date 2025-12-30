package com.emprendedores.crm.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClienteCreateUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    private String telefono;

    private String etiqueta;

    // CORRECCIÓN: Eliminamos @NotNull.
    // Ahora el ID se obtiene del Token en el Controller/Service.
    private Long emprendedorId;
}