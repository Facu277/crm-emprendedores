package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.emprendedor.EmprendedorCreateUpdateDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz de servicio para la administración de perfiles de emprendedores.
 * <p>
 * Proporciona las operaciones necesarias para gestionar la información base 
 * del emprendedor y su identidad visual en la plataforma.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface EmprendedorService {

    /**
     * Registra un nuevo emprendedor en el sistema procesando su foto de perfil.
     * @param dto Datos personales y de negocio.
     * @param fotoPerfil Archivo de imagen para el perfil.
     * @return DTO del emprendedor creado.
     */
    EmprendedorResponseDTO create(EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil);

    /**
     * Actualiza la información de un perfil de emprendedor existente.
     * @param id Identificador único.
     * @param dto Datos modificados.
     * @param fotoPerfil Nueva foto de perfil (opcional).
     * @return DTO actualizado.
     */
    EmprendedorResponseDTO update(Long id, EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil);

    /**
     * Obtiene los datos detallados de un emprendedor por su ID.
     * @param id Identificador único.
     * @return DTO con la información del perfil.
     */
    EmprendedorResponseDTO findById(Long id);

    /**
     * Lista todos los emprendedores registrados (Uso administrativo).
     * @return Lista de perfiles de emprendedores.
     */
    List<EmprendedorResponseDTO> findAll();

    /**
     * Elimina el registro de un emprendedor y limpia sus recursos asociados.
     * @param id Identificador del emprendedor a eliminar.
     */
    void delete(Long id);
}
