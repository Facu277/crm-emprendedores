package com.emprendedores.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Rol extends BaseEntity { // Hereda campos de auditoría

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Column(unique = true)
    private String nombre; // Ej: ADMIN, USER

    // Relación One-to-Many con Usuario (el lado no-owning)
    @OneToMany(mappedBy = "rol")
    private Set<User> users;
}
