package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false)
    public String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // ESTA ES LA CORRECCIÓN PARA EL ERROR DE BUILDER:
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    // Si es true, el token ya no sirve (logout o revocación administrativa)
    public boolean revoked;

    // Si es true, el tiempo de vida expiró (útil para jobs de limpieza de BD)
    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
