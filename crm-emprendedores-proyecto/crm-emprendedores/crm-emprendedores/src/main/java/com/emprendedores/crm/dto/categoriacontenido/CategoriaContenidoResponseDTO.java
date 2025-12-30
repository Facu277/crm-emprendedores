package com.emprendedores.crm.dto.categoriacontenido;

import com.emprendedores.crm.dto.emprendedor.EmprendedorMinResponseDTO;
import com.emprendedores.crm.dto.contenido.ContenidoMinResponseDTO; // Necesario para la lista de contenidos
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaContenidoResponseDTO {
    private Long id;
    private String nombre;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private EmprendedorMinResponseDTO emprendedor;
}
