package com.emprendedores.crm.model;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Clase base abstracta que centraliza los atributos de información personal y contacto.
 * <p>
 * Esta clase extiende de {@link BaseEntity} para heredar la funcionalidad de auditoría.
 * Al estar marcada con @MappedSuperclass, sus campos (nombre, apellido, email, teléfono)
 * se integrarán como columnas en las tablas de las entidades que la extiendan, 
 * como Emprendedores o Clientes.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public abstract class PersonaBase extends BaseEntity {

    /**
     * Nombre o nombres de la persona.
     */
    private String nombre;

    /**
     * Apellido o apellidos de la persona.
     */
    private String apellido;

    /**
     * Dirección de correo electrónico. 
     * Se utiliza generalmente como medio de contacto principal o identificación.
     */
    private String email;

    /**
     * Número de teléfono de contacto.
     */
    private String telefono;
}