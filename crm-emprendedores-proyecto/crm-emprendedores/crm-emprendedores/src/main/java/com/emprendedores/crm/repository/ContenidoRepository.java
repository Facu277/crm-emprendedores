package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    // Obtener contenido asociado a un emprendedor (Aislamiento Multi-Tenant)
    List<Contenido> findByEmprendedorId(Long emprendedorId);

    @Query("SELECT COUNT(c) FROM Contenido c WHERE c.emprendedor.id = :id AND c.fechaProgramada > CURRENT_TIMESTAMP")
    long countContenidosPendientes(Long id);

    long countByCategoriaContenidoId(Long categoriaId);

    // Obtener contenido filtrado por la categoría personalizada del emprendedor
    List<Contenido> findByEmprendedorIdAndCategoriaContenidoId(Long emprendedorId, Long categoriaContenidoId);
}

