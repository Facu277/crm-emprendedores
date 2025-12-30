package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.contenido.ContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.contenido.ContenidoResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.ContenidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contenidos")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    // Definimos el ObjectMapper una sola vez para no repetir código
    private ObjectMapper getSafeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<ContenidoResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestPart("dto") @Valid ContenidoCreateUpdateDTO dto, // Volvemos al DTO directo
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        return new ResponseEntity<>(
                contenidoService.create(dto, user.getEmprendedor().getId(), imagen),
                HttpStatus.CREATED
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<ContenidoResponseDTO> update(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestPart("dto") String dtoStr,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        // Ahora el update también procesará correctamente las fechas (LocalDateTime)
        ContenidoCreateUpdateDTO dto = getSafeObjectMapper().readValue(dtoStr, ContenidoCreateUpdateDTO.class);

        return ResponseEntity.ok(contenidoService.update(id, dto, user.getEmprendedor().getId(), imagen));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<ContenidoResponseDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contenidoService.findByIdAndEmprendedorId(id, user.getEmprendedor().getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<List<ContenidoResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contenidoService.findAllByEmprendedorId(user.getEmprendedor().getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        contenidoService.delete(id, user.getEmprendedor().getId());
        return ResponseEntity.noContent().build();
    }
}