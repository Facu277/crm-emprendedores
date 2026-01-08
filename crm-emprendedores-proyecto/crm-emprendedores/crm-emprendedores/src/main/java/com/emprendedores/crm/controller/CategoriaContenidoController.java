package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.service.CategoriaContenidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controlador REST para la gestión de categorías de contenido multimedia.
 * <p>
 * Provee los endpoints necesarios para que los emprendedores puedan organizar su 
 * material publicitario o informativo en diferentes taxonomías. Todas las operaciones 
 * están protegidas y filtradas por el usuario autenticado (Extraído vía {@link Principal}).
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/categorias-contenido")
@RequiredArgsConstructor
public class CategoriaContenidoController {

    private final CategoriaContenidoService service;

    /**
     * Crea una nueva categoría de contenido para el emprendedor autenticado.
     * @param principal Objeto que contiene la identidad del usuario (username/email).
     * @param dto Datos de la categoría a crear.
     * @return ResponseEntity con la categoría creada y estado 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<CategoriaContenidoResponseDTO> create(
            Principal principal, 
            @RequestBody @Valid CategoriaContenidoCreateUpdateDTO dto) {
        return new ResponseEntity<>(service.create(dto, principal.getName()), HttpStatus.CREATED);
    }

    /**
     * Recupera todas las categorías de contenido pertenecientes al usuario actual.
     * @param principal Identidad del usuario autenticado.
     * @return Lista de categorías en formato DTO.
     */
    @GetMapping
    public ResponseEntity<List<CategoriaContenidoResponseDTO>> getAll(Principal principal) {
        return ResponseEntity.ok(service.findAllByUsername(principal.getName()));
    }

    /**
     * Actualiza una categoría existente verificando la propiedad de la misma.
     * @param id Identificador único de la categoría.
     * @param principal Identidad del usuario para validación de seguridad.
     * @param dto Datos actualizados de la categoría.
     * @return Categoría actualizada con estado 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaContenidoResponseDTO> update(
            @PathVariable Long id, 
            Principal principal, 
            @RequestBody @Valid CategoriaContenidoCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto, principal.getName()));
    }

    /**
     * Elimina una categoría de contenido si pertenece al usuario solicitante.
     * @param id Identificador de la categoría a eliminar.
     * @param principal Identidad del usuario autenticado.
     * @return Respuesta vacía con estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
