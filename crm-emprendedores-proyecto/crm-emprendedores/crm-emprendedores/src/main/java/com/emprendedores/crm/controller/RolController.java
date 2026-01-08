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

/**
 * Controlador REST para la administración de roles de seguridad.
 * <p>
 * Provee los endpoints necesarios para gestionar las autoridades del sistema (ej. ROLE_ADMIN, ROLE_EMPRENDEDOR).
 * Debido a la sensibilidad de estas operaciones, todos los métodos están restringidos 
 * estrictamente a usuarios con el rol administrativo.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    /**
     * Inyección de dependencias del servicio de roles.
     * @param rolService Servicio que contiene la lógica de negocio de roles.
     */
    @Autowired
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Crea un nuevo rol dentro del esquema de seguridad.
     * <p>Endpoint restringido a super-administradores.</p>
     * @param dto Datos del nuevo rol (nombre único).
     * @return ResponseEntity con el rol creado y estado 201 (CREATED).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponseDTO> createRol(
            @RequestBody @Valid RolCreateUpdateDTO dto) {
        RolResponseDTO newRol = rolService.create(dto);
        return new ResponseEntity<>(newRol, HttpStatus.CREATED);
    }

    /**
     * Actualiza la denominación o propiedades de un rol existente.
     * @param id Identificador único del rol.
     * @param dto Datos a actualizar.
     * @return Rol actualizado con estado 200 (OK).
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
     * Recupera la información detallada de un rol por su ID.
     * @param id ID del registro.
     * @return DTO del rol solicitado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponseDTO> getRolById(@PathVariable Long id) {
        RolResponseDTO rol = rolService.findById(id);
        return ResponseEntity.ok(rol);
    }

    /**
     * Obtiene el catálogo completo de roles definidos en la aplicación.
     * @return Lista de todos los roles disponibles.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RolResponseDTO>> getAllRoles() {
        List<RolResponseDTO> roles = rolService.findAll();
        return ResponseEntity.ok(roles);
    }

    /**
     * Elimina un rol del sistema.
     * <p>
     * Nota: Se recomienda validar en la capa de servicio que no existan usuarios 
     * asociados a este rol antes de proceder con el borrado físico.
     * </p>
     * @param id ID del rol a eliminar.
     * @return Respuesta vacía con estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
