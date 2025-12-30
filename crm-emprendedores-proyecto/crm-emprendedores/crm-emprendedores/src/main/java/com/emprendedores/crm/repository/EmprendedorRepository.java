package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Emprendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprendedorRepository extends JpaRepository<Emprendedor, Long> {

    // Metodo de consulta personalizado basado en la herencia de PersonaBase
    boolean existsByEmail(String email);
}