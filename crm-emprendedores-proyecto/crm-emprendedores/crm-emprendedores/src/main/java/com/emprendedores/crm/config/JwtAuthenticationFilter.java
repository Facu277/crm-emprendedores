package com.emprendedores.crm.config;

import com.emprendedores.crm.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que intercepta cada solicitud HTTP.
 * <p>
 * Su función es extraer el token del encabezado 'Authorization', validarlo contra
 * la clave secreta y verificar en la base de datos que no haya sido revocado (Logout).
 * Si es válido, establece la identidad del usuario en el contexto de seguridad de Spring.
 * </p>
 * @author Facundo Alfaro
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String servletPath = request.getServletPath();

        // 1. Whitelist: Omitir validación para rutas públicas (Auth, Swagger)
        if (isPublicPath(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Comprobar formato del encabezado Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // 3. Extracción y validación de Claims
            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. Verificación de revocación (Base de Datos)
                // Esto permite invalidar tokens incluso antes de que expiren.
                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                // 5. Validación final de firma y vigencia
                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Inyección del usuario autenticado en el contexto
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Fail-safe: Si algo falla, se limpia el contexto para denegar el acceso
            SecurityContextHolder.clearContext();
            logger.error("Error en autenticación JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/v1/auth/authenticate") ||
               path.equals("/api/v1/auth/register") ||
               path.contains("/v3/api-docs") ||
               path.contains("/swagger-ui");
    }
}