package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    // Seguridad: Buscar un rol por su nombre (ej. "ADMIN", "USER")
    Optional<Rol> findByNombre(String nombre);
}
