package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.contenido.ContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.contenido.ContenidoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de contenidos y publicaciones.
 * <p>
 * Maneja la lógica de negocio asociada a la creación de materiales, incluyendo
 * la carga de archivos multimedia y la vinculación con categorías personalizadas.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface ContenidoService {

    /**
     * Crea un nuevo contenido junto con su recurso visual.
     * @param dto Datos descriptivos del contenido.
     * @param emprendedorId ID del emprendedor propietario.
     * @param imagen Archivo de imagen adjunto.
     * @return DTO del contenido persistido.
     */
    ContenidoResponseDTO create(ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen);

    /**
     * Actualiza un contenido existente y opcionalmente reemplaza su imagen.
     * @param id Identificador del contenido.
     * @param dto Datos actualizados.
     * @param emprendedorId ID del emprendedor para validar propiedad.
     * @param imagen Nuevo archivo de imagen (opcional).
     * @return DTO actualizado.
     */
    ContenidoResponseDTO update(Long id, ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen);

    /**
     * Recupera un contenido específico validando el acceso mediante el ID del emprendedor.
     * @param id Identificador del contenido.
     * @param emprendedorId ID del emprendedor propietario.
     * @return DTO con la información del contenido.
     */
    ContenidoResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId);

    /**
     * Obtiene el listado de contenidos de un emprendedor.
     * @param emprendedorId ID del emprendedor propietario.
     * @return Lista de contenidos asociados.
     */
    List<ContenidoResponseDTO> findAllByEmprendedorId(Long emprendedorId);

    /**
     * Elimina un contenido y gestiona la remoción de sus archivos asociados.
     * @param id Identificador del contenido.
     * @param emprendedorId ID del emprendedor propietario.
     */
    void delete(Long id, Long emprendedorId);
}
