package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de la gestión de tokens de acceso JWT en la base de datos.
 * <p>
 * Permite el control del ciclo de vida de los tokens, facilitando funciones como 
 * el cierre de sesión (logout), revocación de acceso y validación de expiración.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Recupera todos los tokens que aún son válidos para un usuario específico.
     * <p>
     * Se considera válido un token que no ha expirado y no ha sido revocado. 
     * Esta consulta es vital para invalidar sesiones previas cuando se genera un nuevo acceso.
     * </p>
     * * @param id Identificador único del usuario.
     * @return {@link List} de {@link Token} válidos asociados al usuario.
     */
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id);

    /**
     * Busca un registro de token basado en su cadena de caracteres única.
     * * @param token La cadena del token JWT.
     * @return Un {@link Optional} con la entidad {@link Token} encontrada.
     */
    Optional<Token> findByToken(String token);
}