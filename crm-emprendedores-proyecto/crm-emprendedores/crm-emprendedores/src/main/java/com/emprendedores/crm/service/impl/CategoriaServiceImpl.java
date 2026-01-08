package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.categoria.CategoriaCreateUpdateDTO;
import com.emprendedores.crm.dto.categoria.CategoriaResponseDTO;
import com.emprendedores.crm.model.Categoria;
import com.emprendedores.crm.repository.CategoriaRepository;
import com.emprendedores.crm.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

/**
 * Implementación del servicio para la gestión de categorías globales del sistema.
 * <p>
 * Proporciona la lógica de negocio para administrar las categorías generales 
 * compartidas por todos los usuarios, incluyendo validaciones de unicidad de nombres.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Crea una nueva categoría global verificando que el nombre no esté duplicado.
     * @param dto Datos de la categoría a crear.
     * @return DTO de la categoría persistida.
     * @throws ResponseStatusException si el nombre ya existe (BAD_REQUEST).
     */
    @Override
    @Transactional
    public CategoriaResponseDTO create(CategoriaCreateUpdateDTO dto) {
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new ResponseStatusException(BAD_REQUEST, "La categoría ya existe.");
        }

        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .build();

        return toResponseDTO(categoriaRepository.save(categoria));
    }

    /**
     * Actualiza una categoría global existente.
     * @param id ID de la categoría.
     * @param dto Datos actualizados.
     * @return DTO con la información actualizada.
     * @throws ResponseStatusException si no existe el ID (NOT_FOUND) o el nombre nuevo está duplicado (BAD_REQUEST).
     */
    @Override
    @Transactional
    public CategoriaResponseDTO update(Long id, CategoriaCreateUpdateDTO dto) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada."));

        if (!existente.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe otra categoría con ese nombre.");
        }

        existente.setNombre(dto.getNombre());
        return toResponseDTO(categoriaRepository.save(existente));
    }

    /**
     * Busca una categoría global por su identificador único.
     * @param id Identificador de la categoría.
     * @return DTO con los detalles encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id) {
        return categoriaRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada."));
    }

    /**
     * Recupera el listado completo de categorías globales.
     * @return Lista de categorías disponibles en el sistema.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una categoría global por su ID.
     * @param id Identificador del registro a eliminar.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Categoría no encontrada.");
        }
        categoriaRepository.deleteById(id);
    }

    /**
     * Mapea una entidad Categoria a su DTO de respuesta.
     */
    private CategoriaResponseDTO toResponseDTO(Categoria entity) {
        return CategoriaResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}