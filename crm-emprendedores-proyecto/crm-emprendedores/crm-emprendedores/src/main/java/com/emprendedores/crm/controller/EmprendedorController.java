package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.emprendedor.EmprendedorCreateUpdateDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import com.emprendedores.crm.service.EmprendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emprendedores") // Consistencia con v1
@RequiredArgsConstructor
public class EmprendedorController {

    private final EmprendedorService emprendedorService;

    // Solo un ADMIN podría crear emprendedores "a mano" o registro público (permitall en config)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmprendedorResponseDTO> createEmprendedor(
            @RequestPart("dto") @Valid EmprendedorCreateUpdateDTO dto,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {
        return new ResponseEntity<>(emprendedorService.create(dto, fotoPerfil), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #id == principal.emprendedor.id")
    // ^-- SEGURIDAD: Solo el dueño del perfil o un ADMIN pueden editar
    public ResponseEntity<EmprendedorResponseDTO> updateEmprendedor(
            @PathVariable Long id,
            @RequestPart("dto") @Valid EmprendedorCreateUpdateDTO dto,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {
        return ResponseEntity.ok(emprendedorService.update(id, dto, fotoPerfil));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.emprendedor.id")
    public ResponseEntity<EmprendedorResponseDTO> getEmprendedorById(@PathVariable Long id) {
        return ResponseEntity.ok(emprendedorService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo un ADMIN debe poder ver la lista de todos los clientes del sistema
    public ResponseEntity<List<EmprendedorResponseDTO>> getAllEmprendedores() {
        return ResponseEntity.ok(emprendedorService.findAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo un ADMIN borra emprendedores
    public ResponseEntity<Void> deleteEmprendedor(@PathVariable Long id) {
        emprendedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

