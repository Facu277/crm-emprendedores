package com.emprendedores.crm.dto.categoria;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
