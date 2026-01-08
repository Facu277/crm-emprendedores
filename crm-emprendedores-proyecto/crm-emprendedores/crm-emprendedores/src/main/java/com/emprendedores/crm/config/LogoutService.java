package com.emprendedores.crm.config;

import com.emprendedores.crm.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Implementación personalizada para el cierre de sesión en arquitecturas JWT.
 * <p>
 * Debido a que el JWT es stateless, este servicio marca los tokens en la base de datos
 * como 'expirados' y 'revocados' al momento del cierre de sesión. El filtro de 
 * autenticación consulta este estado en cada petición para denegar el acceso.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Procesa la solicitud de logout invalidando el token en la persistencia.
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        
        if (storedToken != null) {
            // Invalidación lógica del token
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}