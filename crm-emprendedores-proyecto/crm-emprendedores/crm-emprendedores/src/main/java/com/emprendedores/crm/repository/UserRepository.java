package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link User}.
 * <p>
 * Proporciona métodos para la autenticación y la recuperación de información detallada 
 * de los usuarios, integrando capacidades de carga optimizada (Fetch Join).
 * </p>
 * @author Facundo Alfaro
 * @version 1.1
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * Método estándar requerido por la implementación de UserDetailsService en Spring Security.
     * @param username Nombre de usuario único.
     * @return Un {@link Optional} con el usuario encontrado.
     */
    Optional<User> findByUsername(String username);

    /**
     * Recupera un usuario cargando sus relaciones de Emprendedor y Rol en una sola consulta.
     * <p>
     * Utiliza "LEFT JOIN FETCH" para evitar el problema de las N+1 consultas, 
     * optimizando el rendimiento durante el proceso de login o validación de tokens.
     * </p>
     * @param username Nombre de usuario.
     * @return Un {@link Optional} con el usuario y sus relaciones inicializadas.
     */
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.emprendedor " +
            "LEFT JOIN FETCH u.rol " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithDetails(@Param("username") String username);

    /**
     * Verifica la disponibilidad de un nombre de usuario.
     * @param username Nombre de usuario a comprobar.
     * @return {@code true} si ya existe, {@code false} de lo contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Obtiene todos los usuarios vinculados a un emprendedor específico.
     * @param emprendedorId ID del emprendedor.
     * @return Lista de usuarios asociados.
     */
    List<User> findByEmprendedorId(Long emprendedorId);
}