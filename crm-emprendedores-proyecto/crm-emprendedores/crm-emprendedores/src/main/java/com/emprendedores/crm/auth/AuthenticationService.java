package com.emprendedores.crm.auth;

import com.emprendedores.crm.dto.user.UserResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.model.Token;
import com.emprendedores.crm.model.TokenType;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import com.emprendedores.crm.config.JwtService;
import com.emprendedores.crm.repository.EmprendedorRepository;
import com.emprendedores.crm.repository.RolRepository;
import com.emprendedores.crm.repository.TokenRepository;
import com.emprendedores.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de la orquestación de seguridad, autenticación y registro.
 * <p>
 * Gestiona el flujo de creación de usuarios, la validación de credenciales mediante 
 * {@link AuthenticationManager} y el control de persistencia de tokens (Whitelist) 
 * para permitir el cierre de sesión y la invalidación de sesiones previas.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepository rolRepository;
    private final EmprendedorRepository emprendedorRepository;

    /**
     * Registra un nuevo usuario vinculándolo a un rol y a un emprendedor existente.
     * <p>
     * Este proceso cifra la contraseña y genera el par de tokens (Access y Refresh) 
     * iniciales para el usuario.
     * </p>
     * @param request Datos del registro.
     * @return {@link AuthenticationResponse} con los tokens generados.
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        var emprendedor = emprendedorRepository.findById(request.getEmprendedorId())
                .orElseThrow(() -> new RuntimeException("Emprendedor no encontrado"));

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .emprendedor(emprendedor)
                .build();

        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Autentica a un usuario y gestiona la rotación de sus tokens.
     * <p>
     * Al autenticar, se revocan todos los tokens previos del usuario para asegurar 
     * que solo la sesión actual permanezca activa (opcional según política de seguridad).
     * </p>
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Persiste un token en la base de datos vinculado al usuario.
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Invalida lógicamente todos los tokens activos de un usuario.
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Recupera el perfil del usuario autenticado actual.
     * <p>
     * Mapea la información de la entidad {@link User} y su relación con {@link Emprendedor} 
     * a un DTO de respuesta seguro.
     * </p>
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserResponse(String username) {
        var user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EmprendedorResponseDTO empDto = null;
        if (user.getEmprendedor() != null) {
            var emp = user.getEmprendedor();
            empDto = new EmprendedorResponseDTO();
            empDto.setId(emp.getId());
            empDto.setNombre(emp.getNombre());
            empDto.setApellido(emp.getApellido());
            empDto.setEmail(emp.getEmail());
            empDto.setRubro(emp.getRubro());
            empDto.setPais(emp.getPais());
            empDto.setFotoPerfil(emp.getFotoPerfil());
            empDto.setCreadoEn(emp.getCreadoEn());
            empDto.setActualizadoEn(emp.getActualizadoEn());
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .nombre(user.getUsername())
                .email(user.getEmprendedor().getEmail())
                .rol(user.getRol().getNombre())
                .emprendedor(empDto)
                .build();
    }
}