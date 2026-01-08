package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una transacción comercial o venta dentro del sistema.
 * <p>
 * Esta clase hereda de {@link BaseEntity} para el registro automático de auditoría.
 * Almacena los detalles financieros, el método de pago y vincula la operación tanto 
 * con el cliente que realiza la compra como con el emprendedor que registra el ingreso.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "ventas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Venta extends BaseEntity {

    /**
     * Identificador único de la venta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor monetario de la transacción. 
     * Se utiliza {@link BigDecimal} para garantizar precisión decimal en operaciones financieras.
     */
    private BigDecimal monto;

    /**
     * Estado actual de la venta (ej. "Completada", "Pendiente", "Cancelada").
     */
    private String estado;

    /**
     * Fecha y hora en la que se realizó efectivamente la transacción.
     */
    private LocalDateTime fecha;

    /**
     * Medio utilizado para el pago (ej. "Efectivo", "Transferencia", "Tarjeta").
     */
    private String metodoPago;

    /**
     * Información adicional o notas sobre la venta.
     */
    private String descripcion;

    /**
     * Cliente asociado a la transacción.
     * Relación muchos a uno con carga perezosa (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Emprendedor propietario del registro y receptor del ingreso.
     * Relación muchos a uno con carga perezosa (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false)
    private Emprendedor emprendedor;
}
