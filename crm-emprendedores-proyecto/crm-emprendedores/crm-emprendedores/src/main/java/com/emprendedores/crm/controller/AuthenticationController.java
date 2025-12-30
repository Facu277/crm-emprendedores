package com.emprendedores.crm.controller;

import com.emprendedores.crm.auth.AuthenticationRequest;
import com.emprendedores.crm.auth.AuthenticationResponse;
import com.emprendedores.crm.auth.AuthenticationService;
import com.emprendedores.crm.auth.RegisterRequest;
import com.emprendedores.crm.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth") // <-- CORRECCIÓN: Agregamos el /v1 para que coincida con Postman y SecurityConfig
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.getCurrentUserResponse(principal.getName()));
    }
} 
