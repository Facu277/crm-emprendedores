package com.emprendedores.crm.auth;


import com.emprendedores.crm.dto.user.UserResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.model.Token;
import com.emprendedores.crm.model.TokenType;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import com.emprendedores.crm.model.Rol;
import com.emprendedores.crm.model.Emprendedor;
// IMPORTS DIRECTOS (No uses *)

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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    // CAMBIO: De UsuarioRepository a UserRepository
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepository rolRepository;
    private final EmprendedorRepository emprendedorRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        var rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        var emprendedor = emprendedorRepository.findById(request.getEmprendedorId())
                .orElseThrow(() -> new RuntimeException("Emprendedor no encontrado"));

        // Asegúrate de que User.java use @SuperBuilder
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

    private void revokeAllUserTokens(User user) {
        // Asegúrate de que en TokenRepository el método se llame así
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

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
            // Agregamos las fechas para que no salgan null en el DTO
            empDto.setCreadoEn(emp.getCreadoEn());
            empDto.setActualizadoEn(emp.getActualizadoEn());
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .nombre(user.getUsername())
                .email(user.getEmprendedor().getEmail()) // <-- CAMBIO: Asegúrate de usar user.getEmail() no username
                .rol(user.getRol().getNombre())
                .emprendedor(empDto)
                .build();
    }
}
