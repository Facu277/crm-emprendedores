package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.Set;

/**
 * Entidad que representa a un cliente dentro del sistema CRM.
 * <p>
 * Esta clase extiende de {@link PersonaBase}, heredando atributos comunes de identidad.
 * Cada cliente está vinculado a un emprendedor específico y puede tener un historial 
 * de ventas asociado.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "clientes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Cliente extends PersonaBase {

    /**
     * Identificador único del cliente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Etiqueta o segmentación personalizada asignada al cliente 
     * (ej. "VIP", "Frecuente", "Potencial").
     */
    private String etiqueta;

    /**
     * Emprendedor al que pertenece este cliente.
     * Relación muchos a uno con carga perezosa para optimizar consultas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false)
    private Emprendedor emprendedor;

    /**
     * Listado de ventas o transacciones realizadas por este cliente.
     * Relación uno a muchos con persistencia en cascada para gestionar el historial.
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Set<Venta> ventas;
}
