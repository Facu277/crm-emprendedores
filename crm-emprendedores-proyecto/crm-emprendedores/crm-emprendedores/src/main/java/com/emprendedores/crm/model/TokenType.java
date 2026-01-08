package com.emprendedores.crm.model;

/**
 * Enumeración que define los tipos de tokens de autenticación soportados por el sistema.
 * <p>
 * Actualmente, el sistema utiliza el esquema de autenticación basado en el estándar 
 * HTTP Authentication, específicamente mediante el tipo {@code BEARER}.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
public enum TokenType {
    
    /**
     * Tipo de token "Bearer". 
     * Indica que el portador del token tiene acceso a los recursos protegidos 
     * sin necesidad de proporcionar credenciales adicionales en cada petición.
     */
    BEARER
}