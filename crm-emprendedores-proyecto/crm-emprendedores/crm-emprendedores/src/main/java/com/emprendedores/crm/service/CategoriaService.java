package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.categoria.CategoriaCreateUpdateDTO;
import com.emprendedores.crm.dto.categoria.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {

    /**
     * Crea una nueva categoría global, validando la unicidad del nombre.
     */
    CategoriaResponseDTO create(CategoriaCreateUpdateDTO dto);

    /**
     * Actualiza una categoría global existente.
     */
    CategoriaResponseDTO update(Long id, CategoriaCreateUpdateDTO dto);

    /**
     * Obtiene una categoría global por su ID.
     */
    CategoriaResponseDTO findById(Long id);

    /**
     * Obtiene todas las categorías globales.
     */
    List<CategoriaResponseDTO> findAll();

    /**
     * Elimina una categoría global por su ID.
     */
    void delete(Long id);
}

