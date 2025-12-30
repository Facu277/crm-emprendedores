    package com.emprendedores.crm.dto.venta;

    import com.emprendedores.crm.dto.cliente.ClienteMinResponseDTO;
    import com.emprendedores.crm.dto.emprendedor.EmprendedorMinResponseDTO;
    import com.fasterxml.jackson.annotation.JsonFormat;
    import lombok.*;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder // Útil para el mapeo manual que estamos haciendo en el Service
    public class VentaResponseDTO {
        private Long id;
        private BigDecimal monto;
        private String estado;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime fecha;
        private String metodoPago;
        private String descripcion;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime creadoEn;
        private LocalDateTime actualizadoEn;

        private ClienteMinResponseDTO cliente;
        private EmprendedorMinResponseDTO emprendedor;


    }
