package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.categoria.CategoriaCreateUpdateDTO;
import com.emprendedores.crm.dto.categoria.CategoriaResponseDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de categorías globales del sistema.
 * <p>
 * A diferencia de las categorías de contenido, estas son transversales a la plataforma
 * y generalmente gestionadas por administradores.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface CategoriaService {

    /**
     * Crea una nueva categoría global, validando la unicidad del nombre.
     * @param dto Datos de la nueva categoría.
     * @return DTO de la categoría persistida.
     */
    CategoriaResponseDTO create(CategoriaCreateUpdateDTO dto);

    /**
     * Actualiza una categoría global existente por su identificador.
     * @param id Identificador único.
     * @param dto Datos modificados.
     * @return DTO actualizado.
     */
    CategoriaResponseDTO update(Long id, CategoriaCreateUpdateDTO dto);

    /**
     * Obtiene una categoría global por su ID.
     * @param id Identificador único.
     * @return DTO con la información de la categoría.
     */
    CategoriaResponseDTO findById(Long id);

    /**
     * Obtiene el listado completo de categorías globales del sistema.
     * @return Lista de categorías.
     */
    List<CategoriaResponseDTO> findAll();

    /**
     * Elimina una categoría global por su ID.
     * @param id Identificador de la categoría a remover.
     */
    void delete(Long id);
}

