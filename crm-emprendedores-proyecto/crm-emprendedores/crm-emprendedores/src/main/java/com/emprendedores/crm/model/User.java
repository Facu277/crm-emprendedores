package com.emprendedores.crm.model; // Paquete correcto

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Necesario para que funcione con BaseEntity
public class User extends BaseEntity implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = true)
    private Emprendedor emprendedor;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER es útil aquí para cargar roles al login
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    //@Builder.Default
    //private boolean enabled = false;

    // Relación para gestión de tokens (Logout/Refresh)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null || rol.getNombre() == null) return List.of();

        String nombreRol = rol.getNombre().toUpperCase(); // Estandarizamos a mayúsculas

        // Spring Security busca "ROLE_NOMBRE". Si en tu DB el rol se llama solo "ADMIN",
        // aquí lo transformamos en "ROLE_ADMIN".
        if (!nombreRol.startsWith("ROLE_")) {
            nombreRol = "ROLE_" + nombreRol;
        }

        System.out.println("DEBUG - Autoridad generada para " + this.username + ": " + nombreRol);

        return List.of(new SimpleGrantedAuthority(nombreRol));
    }

    // Métodos de UserDetails (Boilerplate estándar)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}


