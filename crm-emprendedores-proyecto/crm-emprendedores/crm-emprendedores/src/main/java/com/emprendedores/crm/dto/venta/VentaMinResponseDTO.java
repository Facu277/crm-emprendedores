package com.emprendedores.crm.dto.venta;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Agrégalo para poder construirlo fácilmente en el Service o Mapper
public class VentaMinResponseDTO {

    private Long id;
    private BigDecimal monto;
    private String estado;
    private LocalDateTime fecha;
}