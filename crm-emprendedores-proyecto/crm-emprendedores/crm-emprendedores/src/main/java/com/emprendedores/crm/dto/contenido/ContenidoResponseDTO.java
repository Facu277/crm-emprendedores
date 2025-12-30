package com.emprendedores.crm.dto.contenido;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoMinResponseDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorMinResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private String imagen;
    private LocalDateTime fechaProgramada;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private EmprendedorMinResponseDTO emprendedor;
    private CategoriaContenidoMinResponseDTO categoriaContenido;
}
