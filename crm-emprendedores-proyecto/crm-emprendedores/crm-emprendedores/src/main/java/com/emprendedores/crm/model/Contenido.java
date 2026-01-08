package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entidad que representa el material o publicaciones (contenidos) creados por un emprendedor.
 * <p>
 * Esta clase hereda de {@link BaseEntity} para el seguimiento de auditoría. Permite gestionar
 * la descripción, el estado, archivos multimedia y la programación de publicaciones, 
 * vinculándolas a un propietario y una categoría específica.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "contenidos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Contenido extends BaseEntity {

    /**
     * Identificador único del contenido.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título o encabezado del contenido.
     */
    private String titulo;

    /**
     * Descripción detallada del contenido.
     * Se utiliza la anotación @Lob para permitir el almacenamiento de textos de gran extensión.
     */
    @Lob 
    private String descripcion;

    /**
     * Estado actual del contenido (ej. "Borrador", "Publicado", "Programado").
     */
    private String estado;

    /**
     * Ruta o URL de la imagen asociada al contenido.
     */
    private String imagen;

    /**
     * Fecha y hora prevista para la publicación del contenido.
     */
    private LocalDateTime fechaProgramada;

    /**
     * Emprendedor responsable o propietario de este contenido.
     * Relación muchos a uno con carga perezosa (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false)
    private Emprendedor emprendedor;

    /**
     * Categoría específica bajo la cual se clasifica este contenido.
     * Relación muchos a uno con carga perezosa (LAZY) hacia {@link CategoriaContenido}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_contenido_id", nullable = false)
    private CategoriaContenido categoriaContenido;
}

