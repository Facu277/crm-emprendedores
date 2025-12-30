package com.emprendedores.crm.model;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public abstract class PersonaBase extends BaseEntity {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}