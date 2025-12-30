package com.emprendedores.crm.dto.emprendedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter @Setter
public class EmprendedorCreateUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    private String telefono;
    private String rubro;
    private String pais;
    private String descripcion;

    private String fotoPerfil;

    @NotBlank(message = "La contraseña es obligatoria para el registro")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    // NOTA: Las colecciones (clientes, contenidos) se gestionan en sus propios controladores/servicios, no directamente aquí.
}
