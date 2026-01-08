package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa los tokens de acceso y seguridad para los usuarios del sistema.
 * <p>
 * Se utiliza principalmente para gestionar la autenticación JWT (JSON Web Token), 
 * permitiendo el control sobre sesiones activas, revocación de acceso y expiración
 * de credenciales temporales.
 * </p>
 * * @author CRM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    /**
     * Identificador único del registro de token en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * Cadena de texto que contiene el valor real del token (JWT).
     * Debe ser única y no nula para garantizar la integridad de la sesión.
     */
    @Column(unique = true, nullable = false)
    public String token;

    /**
     * Tipo de token utilizado. 
     * Por defecto se establece como {@link TokenType#BEARER}.
     * La anotación @Builder.Default asegura que el valor inicial se mantenga al usar el patrón Builder.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    /**
     * Indica si el token ha sido revocado manualmente.
     * Útil para procesos de Logout o invalidación administrativa de sesiones.
     */
    public boolean revoked;

    /**
     * Indica si el token ha superado su tiempo de vida útil.
     * Se utiliza para filtros de seguridad y tareas programadas de limpieza de base de datos.
     */
    public boolean expired;

    /**
     * Usuario al que pertenece este token.
     * Relación muchos a uno con carga perezosa (LAZY) para evitar la sobrecarga 
     * de datos al validar el token.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}