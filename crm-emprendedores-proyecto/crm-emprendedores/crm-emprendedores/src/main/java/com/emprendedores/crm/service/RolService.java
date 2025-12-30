package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.rol.RolCreateUpdateDTO;
import com.emprendedores.crm.dto.rol.RolResponseDTO;

import java.util.List;

public interface RolService {

    /**
     * Crea un nuevo rol, validando que el nombre sea único.
     */
    RolResponseDTO create(RolCreateUpdateDTO dto);

    /**
     * Actualiza un rol existente.
     */
    RolResponseDTO update(Long id, RolCreateUpdateDTO dto);

    /**
     * Obtiene un rol por su ID.
     */
    RolResponseDTO findById(Long id);

    /**
     * Obtiene todos los roles disponibles.
     */
    List<RolResponseDTO> findAll();

    /**
     * Elimina un rol por su ID.
     */
    void delete(Long id);
}
