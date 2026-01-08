package com.emprendedores.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa las categorías de contenido en el sistema.
 * <p>
 * Esta entidad hereda de {@link BaseEntity} para incluir campos de auditoría.
 * Las categorías son globales y sirven para clasificar el contenido creado 
 * por los emprendedores.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "categorias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Categoria extends BaseEntity {

    /**
     * Identificador único de la categoría.
     * Se genera automáticamente mediante una estrategia de identidad en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo de la categoría.
     * Es un campo obligatorio y debe ser único en la base de datos para evitar duplicados.
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Column(unique = true)
    private String nombre;

}
