package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.contenido.ContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.contenido.ContenidoResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.ContenidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST para la gestión de contenidos multimedia y programación de publicaciones.
 * <p>
 * Este controlador permite a los emprendedores subir piezas gráficas, redactar textos
 * y programar fechas de publicación. Maneja peticiones de tipo Multipart para soportar
 * la subida de archivos binarios junto con metadatos en formato JSON.
 * </p>
 * @author Facundo Alfaro
 * @version 1.1
 */
@RestController
@RequestMapping("/api/v1/contenidos")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    /**
     * Configura y retorna un ObjectMapper capaz de procesar tipos de fecha modernos (JSR310).
     * <p>Utilizado para deserializar manualmente los DTOs cuando se reciben como String en partes Multipart.</p>
     * @return ObjectMapper configurado con JavaTimeModule.
     */
    private ObjectMapper getSafeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Crea una nueva pieza de contenido con soporte para archivo multimedia.
     * @param user Usuario autenticado (Emprendedor).
     * @param dto Datos del contenido inyectados como parte de la petición multipart.
     * @param imagen Archivo de imagen opcional.
     * @return Contenido creado con estado 201 (CREATED).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<ContenidoResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestPart("dto") @Valid ContenidoCreateUpdateDTO dto,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        return new ResponseEntity<>(
                contenidoService.create(dto, user.getEmprendedor().getId(), imagen),
                HttpStatus.CREATED
        );
    }

    /**
     * Actualiza un contenido existente y/o su imagen asociada.
     * <p>
     * Debido a las limitaciones de algunos clientes HTTP al enviar JSON complejo y archivos
     * simultáneamente, el DTO se recibe como String y se deserializa manualmente para asegurar
     * la correcta interpretación de campos LocalDateTime.
     * </p>
     * @param id ID del contenido a modificar.
     * @param user Usuario autenticado para validación de propiedad.
     * @param dtoStr Representación en cadena del JSON de actualización.
     * @param imagen Nueva imagen opcional (reemplaza la anterior).
     * @return Contenido actualizado.
     * @throws IOException Si ocurre un error en la deserialización del DTO.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<ContenidoResponseDTO> update(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestPart("dto") String dtoStr,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        ContenidoCreateUpdateDTO dto = getSafeObjectMapper().readValue(dtoStr, ContenidoCreateUpdateDTO.class);

        return ResponseEntity.ok(contenidoService.update(id, dto, user.getEmprendedor().getId(), imagen));
    }

    /**
     * Obtiene los detalles de un contenido validando la propiedad por parte del emprendedor.
     * @param id ID del contenido.
     * @param user Usuario autenticado.
     * @return DTO de respuesta del contenido.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<ContenidoResponseDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contenidoService.findByIdAndEmprendedorId(id, user.getEmprendedor().getId()));
    }

    /**
     * Recupera toda la biblioteca de contenidos del emprendedor actual.
     * @param user Usuario autenticado.
     * @return Lista de contenidos.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<List<ContenidoResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contenidoService.findAllByEmprendedorId(user.getEmprendedor().getId()));
    }

    /**
     * Elimina físicamente un registro de contenido y libera sus recursos asociados.
     * @param id ID del contenido a borrar.
     * @param user Usuario autenticado.
     * @return Estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        contenidoService.delete(id, user.getEmprendedor().getId());
        return ResponseEntity.noContent().build();
    }
}