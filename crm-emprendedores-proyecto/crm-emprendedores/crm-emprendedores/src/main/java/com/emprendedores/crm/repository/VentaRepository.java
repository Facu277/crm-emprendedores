package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Obtener ventas asociadas a un emprendedor (Aislamiento Multi-Tenant)
    List<Venta> findByEmprendedorId(Long emprendedorId);

    // Obtener ventas asociadas a un cliente específico (Dentro de un emprendedor)
    List<Venta> findByClienteIdAndEmprendedorId(Long clienteId, Long emprendedorId);

    // Cuenta ventas de un emprendedor desde una fecha específica
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.emprendedor.id = :id AND v.fecha >= :inicio")
    long countVentasDelMes(Long id, LocalDateTime inicio);

    // Suma el monto de las ventas de un emprendedor desde una fecha específica
    @Query("SELECT SUM(v.monto) FROM Venta v WHERE v.emprendedor.id = :id AND v.fecha >= :inicio")
    BigDecimal sumIngresosDelMes(Long id, LocalDateTime inicio);

    // Para la lista de actividad reciente
    List<Venta> findTop5ByEmprendedorIdOrderByFechaDesc(Long emprendedorId);
        

}

