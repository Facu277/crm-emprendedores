package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.rol.RolCreateUpdateDTO;
import com.emprendedores.crm.dto.rol.RolResponseDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de roles y permisos del sistema.
 * <p>
 * Se encarga de la administración de los niveles de acceso (ej. ADMIN, USER, EMPRENDEDOR)
 * que rigen las políticas de seguridad de la aplicación.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface RolService {

    /**
     * Crea un nuevo rol en el sistema, validando la unicidad del nombre.
     * @param dto Datos para la creación del rol.
     * @return {@link RolResponseDTO} con la información del rol persistido.
     */
    RolResponseDTO create(RolCreateUpdateDTO dto);

    /**
     * Actualiza la información de un rol existente.
     * @param id Identificador único del rol.
     * @param dto Datos actualizados.
     * @return {@link RolResponseDTO} con los cambios aplicados.
     */
    RolResponseDTO update(Long id, RolCreateUpdateDTO dto);

    /**
     * Recupera un rol específico por su identificador.
     * @param id Identificador único.
     * @return DTO con la información del rol.
     */
    RolResponseDTO findById(Long id);

    /**
     * Obtiene el listado completo de todos los roles configurados en el sistema.
     * @return Lista de roles disponibles.
     */
    List<RolResponseDTO> findAll();

    /**
     * Elimina un rol del sistema basándose en su ID.
     * @param id Identificador del rol a remover.
     */
    void delete(Long id);
}
