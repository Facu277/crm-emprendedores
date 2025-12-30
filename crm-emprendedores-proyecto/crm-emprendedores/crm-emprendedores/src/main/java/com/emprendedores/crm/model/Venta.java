package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Venta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal monto;
    private String estado;
    private LocalDateTime fecha;
    private String metodoPago;
    private String descripcion;

    // 1. Relación Many-to-One con Cliente (Transaccional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false) // FK: clienteId
    private Cliente cliente;

    // 2. Relación Many-to-One con Emprendedor (Propiedad/Registro)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false) // FK: emprendedorId
    private Emprendedor emprendedor;
}

