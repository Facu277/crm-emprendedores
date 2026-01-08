package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.emprendedor.EmprendedorCreateUpdateDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import com.emprendedores.crm.service.EmprendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador REST para la gestión de perfiles de emprendedores.
 * <p>
 * Este componente permite administrar la información básica, de contacto y recursos
 * multimedia (fotos de perfil) de los emprendedores. Implementa reglas de seguridad 
 * granulares donde los usuarios pueden gestionar su propio perfil, mientras que el 
 * rol ADMIN retiene el control total sobre todos los registros.
 * </p>
 * @author Facundo Alfaro
 * @version 1.1
 */
@RestController
@RequestMapping("/api/v1/emprendedores")
@RequiredArgsConstructor
public class EmprendedorController {

    private final EmprendedorService emprendedorService;

    /**
     * Crea un nuevo emprendedor en el sistema.
     * <p>
     * Utiliza {@code MULTIPART_FORM_DATA} para recibir simultáneamente el DTO con datos 
     * estructurados y el archivo de imagen de perfil.
     * </p>
     * @param dto Datos del emprendedor (nombre, email, rubro, etc.).
     * @param fotoPerfil Archivo de imagen opcional para el perfil.
     * @return ResponseEntity con el emprendedor creado y estado 201 (CREATED).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmprendedorResponseDTO> createEmprendedor(
            @RequestPart("dto") @Valid EmprendedorCreateUpdateDTO dto,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {
        return new ResponseEntity<>(emprendedorService.create(dto, fotoPerfil), HttpStatus.CREATED);
    }

    /**
     * Actualiza la información de un emprendedor existente.
     * <p>
     * Seguridad: Solo el administrador o el dueño del perfil (comparando el ID 
     * de la URL con el ID del emprendedor en el token) pueden realizar esta acción.
     * </p>
     * @param id Identificador único del emprendedor a actualizar.
     * @param dto Datos actualizados.
     * @param fotoPerfil Nueva foto de perfil opcional.
     * @return Perfil actualizado con estado 200 (OK).
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #id == principal.emprendedor.id")
    public ResponseEntity<EmprendedorResponseDTO> updateEmprendedor(
            @PathVariable Long id,
            @RequestPart("dto") @Valid EmprendedorCreateUpdateDTO dto,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {
        return ResponseEntity.ok(emprendedorService.update(id, dto, fotoPerfil));
    }

    /**
     * Obtiene el perfil detallado de un emprendedor.
     * @param id Identificador único.
     * @return DTO de respuesta con los datos del perfil.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.emprendedor.id")
    public ResponseEntity<EmprendedorResponseDTO> getEmprendedorById(@PathVariable Long id) {
        return ResponseEntity.ok(emprendedorService.findById(id));
    }

    /**
     * Recupera el listado de todos los emprendedores registrados.
     * <p>Restringido exclusivamente al rol ADMIN para fines de auditoría y gestión global.</p>
     * @return Lista completa de emprendedores.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmprendedorResponseDTO>> getAllEmprendedores() {
        return ResponseEntity.ok(emprendedorService.findAll());
    }

    /**
     * Elimina de forma permanente un perfil de emprendedor.
     * <p>Esta operación suele incluir la eliminación en cascada de sus usuarios y datos asociados.</p>
     * @param id ID del emprendedor a eliminar.
     * @return Estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmprendedor(@PathVariable Long id) {
        emprendedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

