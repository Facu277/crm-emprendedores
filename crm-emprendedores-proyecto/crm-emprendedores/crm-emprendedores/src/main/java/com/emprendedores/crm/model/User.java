package com.emprendedores.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema, integrando la seguridad de Spring Security.
 * <p>
 * Implementa {@link UserDetails} para permitir que Spring Security gestione la autenticación
 * y autorización. Hereda de {@link BaseEntity} para mantener campos de auditoría y se vincula
 * opcionalmente a un {@link Emprendedor}.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.1
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único utilizado para el inicio de sesión.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Contraseña cifrada del usuario.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Emprendedor asociado a esta cuenta de usuario. 
     * Puede ser nulo si el usuario es un administrador global.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = true)
    private Emprendedor emprendedor;

    /**
     * Rol asignado al usuario. 
     * Se carga de forma EAGER para disponer de los permisos inmediatamente durante el login.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    /**
     * Lista de tokens asociados al usuario para la gestión de sesiones y logout.
     * Se ignora en la serialización JSON para evitar ciclos y exposición de datos sensibles.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;

    /**
     * Retorna las autoridades concedidas al usuario basándose en su rol.
     * <p>
     * Transforma el nombre del rol al formato requerido por Spring Security (prefijo ROLE_).
     * </p>
     * @return Colección de autoridades (roles) del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null || rol.getNombre() == null) return List.of();

        String nombreRol = rol.getNombre().toUpperCase();

        if (!nombreRol.startsWith("ROLE_")) {
            nombreRol = "ROLE_" + nombreRol;
        }

        return List.of(new SimpleGrantedAuthority(nombreRol));
    }

    /** Indica si la cuenta del usuario ha expirado. */
    @Override public boolean isAccountNonExpired() { return true; }

    /** Indica si el usuario está bloqueado o desbloqueado. */
    @Override public boolean isAccountNonLocked() { return true; }

    /** Indica si las credenciales (contraseña) han expirado. */
    @Override public boolean isCredentialsNonExpired() { return true; }

    /** Indica si el usuario está habilitado para operar en el sistema. */
    @Override public boolean isEnabled() { return true; }
}
