package com.emprendedores.crm.dto.contenido;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoMinResponseDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorMinResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

// ContenidoCreateUpdateDTO.java
@Getter @Setter
public class ContenidoCreateUpdateDTO {
    @NotBlank(message = "El título del contenido es obligatorio")
    private String titulo;

    private String descripcion;
    private String estado;
    private String imagen;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
    private LocalDateTime fechaProgramada;

    // Se mantiene para el modelo, pero NO se valida con @NotNull
    private Long emprendedorId;

    @NotNull(message = "El ID de la categoría de contenido es obligatorio")
    private Long categoriaContenidoId;
}

