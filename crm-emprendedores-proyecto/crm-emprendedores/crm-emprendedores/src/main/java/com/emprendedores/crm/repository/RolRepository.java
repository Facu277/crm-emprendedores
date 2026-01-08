package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link Rol}.
 * <p>
 * Proporciona métodos para acceder a la configuración de roles del sistema, 
 * fundamentales para la lógica de autorización en la capa de seguridad.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol específico basado en su nombre único.
     * <p>Utilizado durante la asignación de permisos y validación de seguridad (ej. "ADMIN", "USER").</p>
     * * @param nombre El nombre del rol a buscar.
     * @return Un {@link Optional} que contiene el rol si se encuentra, o vacío si no.
     */
    Optional<Rol> findByNombre(String nombre);
}
