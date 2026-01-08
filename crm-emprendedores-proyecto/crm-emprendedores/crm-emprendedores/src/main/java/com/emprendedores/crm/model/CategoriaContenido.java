package com.emprendedores.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Entidad que representa la clasificación específica de contenidos creada por un emprendedor.
 * <p>
 * A diferencia de las categorías globales, esta clase permite que cada emprendedor 
 * defina sus propias etiquetas. Hereda de {@link BaseEntity} para auditoría.
 * </p>
 * <p>
 * La tabla cuenta con una restricción de unicidad que impide que un mismo emprendedor 
 * cree dos categorías con el mismo nombre.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "categoria_contenidos",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nombre", "emprendedor_id"})
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class CategoriaContenido extends BaseEntity {

    /**
     * Identificador único de la categoría de contenido.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la categoría definida por el usuario.
     * Es un campo obligatorio.
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    /**
     * Emprendedor propietario de esta categoría.
     * Relación muchos a uno con carga perezosa (LAZY) para optimizar el rendimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false)
    private Emprendedor emprendedor;

    /**
     * Conjunto de contenidos asociados a esta categoría.
     * Relación uno a muchos con persistencia en cascada y eliminación de huérfanos.
     */
    @OneToMany(mappedBy = "categoriaContenido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contenido> contenidos;
}