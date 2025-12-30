package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Metodo de unicidad para la categoría global
    boolean existsByNombre(String nombre);
}

