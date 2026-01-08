package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link Venta}.
 * <p>
 * Centraliza las operaciones transaccionales y de reporte financiero del sistema, 
 * asegurando el aislamiento de datos por emprendedor (Multi-Tenancy).
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    /**
     * Recupera todas las ventas pertenecientes a un emprendedor.
     * @param emprendedorId Identificador del emprendedor propietario.
     * @return Lista completa de ventas del emprendedor.
     */
    List<Venta> findByEmprendedorId(Long emprendedorId);

    /**
     * Filtra las ventas por cliente, garantizando que pertenezcan al emprendedor autenticado.
     * @param clienteId ID del cliente.
     * @param emprendedorId ID del emprendedor propietario.
     * @return Lista de ventas realizadas al cliente específico dentro del contexto del emprendedor.
     */
    List<Venta> findByClienteIdAndEmprendedorId(Long clienteId, Long emprendedorId);

    /**
     * Cuenta el volumen de ventas realizadas desde una fecha determinada (ej. inicio de mes).
     * @param id ID del emprendedor.
     * @param inicio Fecha de corte para el conteo.
     * @return Cantidad total de ventas en el periodo.
     */
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.emprendedor.id = :id AND v.fecha >= :inicio")
    long countVentasDelMes(@Param("id") Long id, @Param("inicio") LocalDateTime inicio);

    /**
     * Calcula los ingresos totales (suma de montos) generados por un emprendedor en un periodo.
     * @param id ID del emprendedor.
     * @param inicio Fecha de corte para la sumatoria.
     * @return {@link BigDecimal} con el total de ingresos.
     */
    @Query("SELECT SUM(v.monto) FROM Venta v WHERE v.emprendedor.id = :id AND v.fecha >= :inicio")
    BigDecimal sumIngresosDelMes(@Param("id") Long id, @Param("inicio") LocalDateTime inicio);

    /**
     * Obtiene las últimas 5 ventas registradas, ordenadas de forma cronológica descendente.
     * Utilizado para dashboards y listas de actividad reciente.
     * @param emprendedorId ID del emprendedor propietario.
     * @return Lista de las 5 ventas más recientes.
     */
    List<Venta> findTop5ByEmprendedorIdOrderByFechaDesc(Long emprendedorId);
}

