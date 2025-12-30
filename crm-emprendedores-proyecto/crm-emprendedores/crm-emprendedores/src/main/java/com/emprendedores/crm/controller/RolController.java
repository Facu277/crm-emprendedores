package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.rol.RolCreateUpdateDTO;
import com.emprendedores.crm.dto.rol.RolResponseDTO;
import com.emprendedores.crm.service.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    @Autowired
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Crea un nuevo Rol (generalmente restringido a super-administradores).
     * URL: POST /api/roles
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponseDTO> createRol(
            @RequestBody @Valid RolCreateUpdateDTO dto) {

        RolResponseDTO newRol = rolService.create(dto);
        return new ResponseEntity<>(newRol, HttpStatus.CREATED);
    }

    /**
     * Actualiza un Rol existente.
     * URL: PUT /api/roles/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponseDTO> updateRol(
            @PathVariable Long id,
            @RequestBody @Valid RolCreateUpdateDTO dto) {

        RolResponseDTO updatedRol = rolService.update(id, dto);
        return ResponseEntity.ok(updatedRol);
    }

    /**
     * Obtiene un Rol por su ID.
     * URL: GET /api/roles/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponseDTO> getRolById(@PathVariable Long id) {
        RolResponseDTO rol = rolService.findById(id);
        return ResponseEntity.ok(rol);
    }

    /**
     * Obtiene todos los Roles disponibles.
     * URL: GET /api/roles
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RolResponseDTO>> getAllRoles() {
        List<RolResponseDTO> roles = rolService.findAll();
        return ResponseEntity.ok(roles);
    }

    /**
     * Elimina un Rol.
     * URL: DELETE /api/roles/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
