package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Emprendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link Emprendedor}.
 * <p>
 * Expone métodos para la administración de los perfiles de emprendedores, 
 * aprovechando los atributos heredados de la clase base de personas.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface EmprendedorRepository extends JpaRepository<Emprendedor, Long> {

    /**
     * Verifica si existe un emprendedor registrado con un correo electrónico específico.
     * <p>Este método es crítico durante el proceso de registro para evitar duplicidad de cuentas.</p>
     * * @param email Correo electrónico a validar.
     * @return {@code true} si el email ya se encuentra en la base de datos, {@code false} en caso contrario.
     */
    boolean existsByEmail(String email);
}