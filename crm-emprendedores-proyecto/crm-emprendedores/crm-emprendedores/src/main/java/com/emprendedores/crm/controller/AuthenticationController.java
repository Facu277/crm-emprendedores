package com.emprendedores.crm.controller;

import com.emprendedores.crm.auth.AuthenticationRequest;
import com.emprendedores.crm.auth.AuthenticationResponse;
import com.emprendedores.crm.auth.AuthenticationService;
import com.emprendedores.crm.auth.RegisterRequest;
import com.emprendedores.crm.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controlador REST encargado de los procesos de autenticación y registro de usuarios.
 * <p>
 * Provee los endpoints necesarios para el intercambio de credenciales por tokens JWT,
 * el registro de nuevos emprendedores y la recuperación de la identidad del usuario
 * autenticado en la sesión actual.
 * </p>
 * @author Facundo Alfaro
 * @version 1.1
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    /**
     * Registra un nuevo emprendedor en la plataforma.
     * <p>
     * Este endpoint orquestará la creación del perfil de emprendedor y sus 
     * credenciales de acceso de forma atómica.
     * </p>
     * @param request Objeto con los datos de registro (email, nombre, contraseña, etc.).
     * @return {@link AuthenticationResponse} que contiene el token JWT generado para la nueva cuenta.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Autentica a un usuario existente mediante sus credenciales.
     * <p>
     * Valida el email y password contra la base de datos y, si son correctos,
     * retorna un token JWT firmado para ser utilizado en las cabeceras Authorization.
     * </p>
     * @param request Objeto con email y password.
     * @return {@link AuthenticationResponse} con el token de acceso.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * Recupera la información detallada del usuario que ha iniciado sesión.
     * <p>
     * Utiliza el objeto {@link Principal} inyectado por Spring Security para identificar
     * al usuario a través del token JWT enviado en la petición. Este endpoint es crítico
     * para la persistencia del estado en aplicaciones React/Vue/Angular.
     * </p>
     * @param principal Representación del usuario autenticado actual.
     * @return {@link UserResponseDTO} con los datos del perfil y rol del usuario.
     * @throws ResponseEntity con estado 401 si no hay una sesión activa.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.getCurrentUserResponse(principal.getName()));
    }
}
