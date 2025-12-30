package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEmprendedorId(Long emprendedorId);

    boolean existsByEmailAndEmprendedorId(String email, Long emprendedorId);

    // Nuevo metodo para buscar asegurando que pertenece al dueño
    Optional<Cliente> findByIdAndEmprendedorId(Long id, Long emprendedorId);

    long countByEmprendedorId(Long emprendedorId);

    
}

