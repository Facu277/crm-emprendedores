package com.emprendedores.crm.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio encargado de la generación, extracción y validación de tokens JWT.
 * <p>
 * Provee soporte para tokens de acceso y tokens de refresco, permitiendo mantener
 * sesiones seguras y extender la duración de las mismas sin requerir re-autenticación constante.
 * </p>
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long jwtExpiration;

    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpiration;

    /**
     * Extrae el nombre de usuario (subject) contenido en el cuerpo del token.
     * @param token Cadena JWT.
     * @return Nombre de usuario del propietario del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un atributo (claim) específico del token utilizando un resolvedor funcional.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token de acceso estándar para un usuario.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token de acceso incluyendo atributos adicionales personalizados.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration, secretKey);
    }

    /**
     * Genera un Refresh Token con una expiración prolongada y una firma distinta.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration, refreshSecretKey);
    }

    /**
     * Construcción genérica del JWT.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String secret
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(secret))
                .compact();
    }

    /**
     * Verifica si el token pertenece al usuario y si no ha expirado.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parsea el token y valida su integridad criptográfica.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decodifica la clave secreta en BASE64 y genera la clave HMAC-SHA.
     */
    private SecretKey getSignInKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
