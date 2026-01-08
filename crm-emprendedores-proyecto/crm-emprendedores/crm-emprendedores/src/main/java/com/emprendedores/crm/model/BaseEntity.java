package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * Clase base abstracta que proporciona campos de auditoría para las entidades del sistema.
 * * Esta clase utiliza JPA Auditing para gestionar automáticamente las marcas de tiempo
 * de creación y modificación de los registros. Al estar marcada con @MappedSuperclass,
 * sus atributos se mapearán como columnas en las tablas de las entidades que la extiendan.
 * * @author Facundo ALfaro
 * @version 1.0
 */
@MappedSuperclass
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Fecha y hora en la que se creó el registro por primera vez.
     * Este campo se genera automáticamente y no puede ser modificado posteriormente.
     */
    @CreatedDate
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    /**
     * Fecha y hora de la última actualización realizada sobre el registro.
     * Este campo se actualiza automáticamente cada vez que la entidad es modificada.
     */
    @LastModifiedDate
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}