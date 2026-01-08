package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.categoria.CategoriaCreateUpdateDTO;
import com.emprendedores.crm.dto.categoria.CategoriaResponseDTO;
import com.emprendedores.crm.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de categorías globales del sistema.
 * <p>
 * Este componente permite administrar las categorías generales que clasifican los rubros
 * de los emprendedores. Utiliza seguridad declarativa para restringir las operaciones
 * de escritura a usuarios con privilegios administrativos o de emprendedor, según la política.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    /**
     * Registra una nueva categoría global en el sistema.
     * <p>Solo accesible para roles administrativos o emprendedores con permisos de gestión.</p>
     * @param dto Datos de la categoría (nombre, descripción, etc.).
     * @return ResponseEntity con la categoría creada y estado 201 (CREATED).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<CategoriaResponseDTO> create(@RequestBody @Valid CategoriaCreateUpdateDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    /**
     * Recupera el listado completo de categorías globales.
     * <p>Endpoint público para todos los usuarios autenticados del sistema.</p>
     * @return Lista de categorías en formato DTO.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<List<CategoriaResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Busca una categoría específica por su identificador único.
     * @param id Identificador de la categoría.
     * @return DTO de la categoría solicitada.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<CategoriaResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Actualiza la información de una categoría global existente.
     * @param id Identificador de la categoría a modificar.
     * @param dto Datos actualizados.
     * @return Categoría actualizada con estado 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<CategoriaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Elimina una categoría global del sistema.
     * <p>Se debe tener precaución ya que esto puede afectar a los emprendedores asociados.</p>
     * @param id Identificador de la categoría a eliminar.
     * @return Respuesta vacía con estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPRENDEDOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}