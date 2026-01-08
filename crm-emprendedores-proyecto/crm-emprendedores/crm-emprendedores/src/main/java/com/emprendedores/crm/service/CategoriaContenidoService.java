package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de categorías de contenido personalizadas.
 * <p>
 * Define las operaciones permitidas para que un emprendedor organice su propio material,
 * asegurando que cada acción esté vinculada a su identidad de usuario.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface CategoriaContenidoService {

    /**
     * Crea una nueva categoría de contenido asociada al usuario autenticado.
     * @param dto Datos para la creación de la categoría.
     * @param username Nombre de usuario del emprendedor propietario.
     * @return {@link CategoriaContenidoResponseDTO} con los datos de la categoría creada.
     */
    CategoriaContenidoResponseDTO create(CategoriaContenidoCreateUpdateDTO dto, String username);

    /**
     * Actualiza una categoría existente, validando la propiedad del recurso.
     * @param id Identificador de la categoría.
     * @param dto Datos actualizados.
     * @param username Nombre de usuario para validar permisos.
     * @return {@link CategoriaContenidoResponseDTO} actualizado.
     */
    CategoriaContenidoResponseDTO update(Long id, CategoriaContenidoCreateUpdateDTO dto, String username);

    /**
     * Busca una categoría específica vinculada al usuario.
     * @param id ID de la categoría.
     * @param username Nombre de usuario del propietario.
     * @return DTO con la información de la categoría.
     */
    CategoriaContenidoResponseDTO findByIdAndUsername(Long id, String username);

    /**
     * Recupera todas las categorías de contenido pertenecientes a un emprendedor.
     * @param username Nombre de usuario del emprendedor.
     * @return Lista de categorías asociadas.
     */
    List<CategoriaContenidoResponseDTO> findAllByUsername(String username);

    /**
     * Elimina una categoría personalizada del sistema.
     * @param id ID de la categoría a eliminar.
     * @param username Nombre de usuario para validar propiedad.
     */
    void delete(Long id, String username);
}