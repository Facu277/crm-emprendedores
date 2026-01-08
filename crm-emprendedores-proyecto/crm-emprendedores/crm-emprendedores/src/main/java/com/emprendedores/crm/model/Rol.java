package com.emprendedores.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Entidad que representa los roles o niveles de acceso dentro del sistema.
 * <p>
 * Esta clase hereda de {@link BaseEntity} para incluir marcas de tiempo de auditoría.
 * Define los permisos y capacidades que los usuarios ({@link User}) pueden tener
 * en la plataforma (ej. ADMIN, USER, EMPRENDEDOR).
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Rol extends BaseEntity {

    /**
     * Identificador único del rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo del rol. 
     * Este campo es obligatorio, debe ser único y se utiliza para la lógica 
     * de autorización en el sistema (ej. "ROLE_ADMIN").
     */
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Column(unique = true)
    private String nombre;

    /**
     * Conjunto de usuarios que tienen asignado este rol.
     * Relación uno a muchos mapeada por el atributo "rol" en la entidad {@link User}.
     */
    @OneToMany(mappedBy = "rol")
    private Set<User> users;
}
