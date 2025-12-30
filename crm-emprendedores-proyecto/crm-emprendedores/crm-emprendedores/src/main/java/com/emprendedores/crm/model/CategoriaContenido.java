package com.emprendedores.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "categoria_contenidos",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nombre", "emprendedor_id"})
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class CategoriaContenido extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    // 1. Relación Many-to-One con Emprendedor (Propiedad)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false) // FK: emprendedorId
    private Emprendedor emprendedor;

    // 2. Relación One-to-Many con Contenido (Clasificación)
    @OneToMany(mappedBy = "categoriaContenido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contenido> contenidos;
}