package com.emprendedores.crm.dto.venta;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class VentaCreateUpdateDTO {

    @NotNull(message = "El monto de la venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotBlank(message = "El estado de la venta es obligatorio")
    private String estado;

    // Opcional: Quitar el @NotNull para que que el servidor
    // asigne la fecha actual automáticamente si viene nula.
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
    private LocalDateTime fecha;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    private String descripcion;

    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    
    // Lo obtenemos del Token en el Controller.
    private Long emprendedorId;
}