package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad encargada de gestionar los tokens de verificación para procesos de seguridad.
 * <p>
 * Se utiliza para validar acciones críticas que requieren confirmación vía canal externo (como el email),
 * asegurando la identidad del usuario antes de proceder con el registro o el cambio de credenciales.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens_verificacion")
public class VerificationToken {

    /**
     * Identificador único del token de verificación en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cadena alfanumérica única enviada al usuario (generalmente vía URL).
     */
    private String token;

    /**
     * Usuario vinculado al proceso de verificación.
     * Relación uno a uno (OneToOne) con carga inmediata (EAGER) para validar 
     * rápidamente al usuario al momento de recibir el token.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Fecha y hora límite en la que el token deja de ser válido.
     */
    private LocalDateTime fechaExpiracion;

    /**
     * Propósito del token en el sistema.
     * Valores esperados: "REGISTRO" (confirmación de cuenta) o "PASSWORD_RESET" (recuperación de contraseña).
     */
    private String tipo;
}