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

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

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

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id) {
        return categoriaRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Categoría no encontrada.");
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaResponseDTO toResponseDTO(Categoria entity) {
        return CategoriaResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}