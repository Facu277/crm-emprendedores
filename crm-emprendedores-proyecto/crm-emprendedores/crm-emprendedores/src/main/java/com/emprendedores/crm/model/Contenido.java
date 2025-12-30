package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "contenidos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Contenido extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Lob // Para texto largo
    private String descripcion;

    private String estado;
    private String imagen;
    private LocalDateTime fechaProgramada;

    // 1. Relación Many-to-One con Emprendedor (Propiedad)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false) // FK: emprendedorId
    private Emprendedor emprendedor;

    // 2. Relación Many-to-One con CategoriaContenido (Clasificación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_contenido_id", nullable = false) // FK: categoriaId (asumimos que es el ID de CategoriaContenido)
    private CategoriaContenido categoriaContenido;
}

