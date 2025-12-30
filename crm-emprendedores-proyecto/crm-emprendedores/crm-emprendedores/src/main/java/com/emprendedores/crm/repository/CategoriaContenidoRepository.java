package com.emprendedores.crm.repository;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaContenidoRepository extends JpaRepository<CategoriaContenido, Long> {
    List<CategoriaContenido> findByEmprendedorId(Long emprendedorId);
    boolean existsByNombreAndEmprendedorId(String nombre, Long emprendedorId);
}