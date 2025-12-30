package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.CategoriaContenidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias-contenido")
@RequiredArgsConstructor
public class CategoriaContenidoController {

    private final CategoriaContenidoService service;

    @PostMapping
    public ResponseEntity<CategoriaContenidoResponseDTO> create(Principal principal, @RequestBody @Valid CategoriaContenidoCreateUpdateDTO dto) {
        return new ResponseEntity<>(service.create(dto, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaContenidoResponseDTO>> getAll(Principal principal) {
        return ResponseEntity.ok(service.findAllByUsername(principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaContenidoResponseDTO> update(@PathVariable Long id, Principal principal, @RequestBody @Valid CategoriaContenidoCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
