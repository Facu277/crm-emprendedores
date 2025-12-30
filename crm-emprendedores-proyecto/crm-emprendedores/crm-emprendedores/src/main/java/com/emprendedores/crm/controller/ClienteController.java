package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.cliente.ClienteCreateUpdateDTO;
import com.emprendedores.crm.dto.cliente.ClienteResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes") // URL estándar v1
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Crear un Cliente.
     * El ID del emprendedor se toma automáticamente del usuario autenticado (@AuthenticationPrincipal).
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<ClienteResponseDTO> createCliente(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ClienteCreateUpdateDTO dto) {
        return new ResponseEntity<>(clienteService.create(dto, user.getEmprendedor().getId()), HttpStatus.CREATED);
    }

    /**
     * Obtener todos los clientes del emprendedor logueado.
     */
    @GetMapping
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clienteService.findAllByEmprendedorId(user.getEmprendedor().getId()));
    }

    /**
     * Obtener un cliente específico validando propiedad.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<ClienteResponseDTO> getClienteById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        ClienteResponseDTO cliente = clienteService.findByIdAndEmprendedorId(id, user.getEmprendedor().getId());
        return ResponseEntity.ok(cliente);
    }

    /**
     * Eliminar un cliente asegurando pertenencia.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPRENDEDOR')") // El service ya valida que le pertenezca
    public ResponseEntity<Void> deleteCliente(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        clienteService.delete(id, user.getEmprendedor().getId());
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<ClienteResponseDTO> updateCliente(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ClienteCreateUpdateDTO dto) {

        // El service debe recibir el ID del cliente, el DTO con nuevos datos
        // y el ID del emprendedor para validar que sea suyo.
        ClienteResponseDTO actualizado = clienteService.update(id, dto, user.getEmprendedor().getId());
        return ResponseEntity.ok(actualizado);
    }
}

