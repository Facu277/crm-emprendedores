package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.repository.CategoriaContenidoRepository;
import com.emprendedores.crm.repository.ContenidoRepository;
import com.emprendedores.crm.repository.UserRepository;
import com.emprendedores.crm.service.CategoriaContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la gestión de categorías de contenido personalizadas.
 * <p>
 * Esta clase centraliza la lógica de negocio para que cada emprendedor pueda organizar
 * su contenido de manera aislada, implementando validaciones de seguridad y 
 * restricciones de integridad referencial.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CategoriaContenidoServiceImpl implements CategoriaContenidoService {

    private final CategoriaContenidoRepository repository;
    private final UserRepository userRepository;
    private final ContenidoRepository contenidoRepository;

    /**
     * Crea una categoría validando que el nombre no esté duplicado para el mismo emprendedor.
     * @param dto Datos de la categoría.
     * @param username Usuario que realiza la acción.
     * @return DTO de la categoría creada.
     * @throws ResponseStatusException si el nombre ya existe (BAD_REQUEST).
     */
    @Override
    @Transactional
    public CategoriaContenidoResponseDTO create(CategoriaContenidoCreateUpdateDTO dto, String username) {
        var user = obtenerUsuario(username);
        Long empId = user.getEmprendedor().getId();

        if (repository.existsByNombreAndEmprendedorId(dto.getNombre(), empId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe esta categoría.");
        }

        var categoria = CategoriaContenido.builder()
                .nombre(dto.getNombre())
                .emprendedor(user.getEmprendedor())
                .build();

        return toResponseDTO(repository.save(categoria));
    }

    /**
     * Actualiza una categoría existente verificando primero la propiedad del recurso.
     * @param id ID de la categoría.
     * @param dto Nuevos datos.
     * @param username Usuario para validación de propiedad.
     * @return DTO actualizado.
     */
    @Override
    @Transactional
    public CategoriaContenidoResponseDTO update(Long id, CategoriaContenidoCreateUpdateDTO dto, String username) {
        var user = obtenerUsuario(username);
        var existente = repository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(user.getEmprendedor().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        if (!existente.getNombre().equalsIgnoreCase(dto.getNombre())) {
            if (repository.existsByNombreAndEmprendedorId(dto.getNombre(), user.getEmprendedor().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre ya en uso");
            }
            existente.setNombre(dto.getNombre());
        }
        return toResponseDTO(repository.save(existente));
    }

    /**
     * Lista todas las categorías asociadas al emprendedor del usuario actual.
     * @param username Identificador del usuario.
     * @return Lista de categorías filtradas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaContenidoResponseDTO> findAllByUsername(String username) {
        var user = obtenerUsuario(username);
        return repository.findByEmprendedorId(user.getEmprendedor().getId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera una categoría por ID asegurando que pertenezca al usuario solicitante.
     * @param id ID de la categoría.
     * @param username Identificador del usuario.
     * @return DTO de la categoría.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoriaContenidoResponseDTO findByIdAndUsername(Long id, String username) {
        var user = obtenerUsuario(username);
        return repository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(user.getEmprendedor().getId()))
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    /**
     * Elimina una categoría siempre que no tenga contenidos asociados.
     * @param id ID de la categoría.
     * @param username Usuario que solicita la eliminación.
     * @throws ResponseStatusException si existen contenidos vinculados (BAD_REQUEST).
     */
    @Override
    @Transactional
    public void delete(Long id, String username) {
        var user = obtenerUsuario(username);
        var cat = repository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(user.getEmprendedor().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        long cantidadContenidos = contenidoRepository.countByCategoriaContenidoId(id);
        if (cantidadContenidos > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar: Esta categoría tiene " + cantidadContenidos + " contenidos asociados. Reclasifícalos primero.");
        }

        repository.delete(cat);
    }

    /**
     * Método auxiliar para recuperar el usuario de la base de datos.
     */
    private User obtenerUsuario(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    /**
     * Convierte la entidad en un DTO de respuesta.
     */
    private CategoriaContenidoResponseDTO toResponseDTO(CategoriaContenido entity) {
        return CategoriaContenidoResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}