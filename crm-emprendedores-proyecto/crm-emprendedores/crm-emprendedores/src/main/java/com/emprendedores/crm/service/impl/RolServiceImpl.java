package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.rol.RolCreateUpdateDTO;
import com.emprendedores.crm.dto.rol.RolResponseDTO;
import com.emprendedores.crm.model.Rol;
import com.emprendedores.crm.repository.RolRepository;
import com.emprendedores.crm.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Implementación del servicio para la gestión de roles de usuario.
 * <p>
 * Esta clase proporciona la lógica necesaria para administrar los roles del sistema,
 * los cuales determinan los permisos y el acceso de los usuarios a los diferentes
 * módulos de la aplicación (ej. ADMIN, EMPRENDEDOR).
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    /**
     * Constructor para la inyección de dependencias.
     * @param rolRepository Repositorio para el acceso a datos de roles.
     */
    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Crea un nuevo rol en el sistema validando que el nombre sea único.
     * @param dto Datos para la creación del rol.
     * @return {@link RolResponseDTO} con el rol persistido.
     * @throws ResponseStatusException si el nombre del rol ya existe (BAD_REQUEST).
     */
    @Override
    @Transactional
    public RolResponseDTO create(RolCreateUpdateDTO dto) {
        if (rolRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe un rol con el nombre: " + dto.getNombre());
        }

        Rol rol = toRolEntity(dto);
        Rol savedRol = rolRepository.save(rol);

        return toRolResponseDTO(savedRol);
    }

    /**
     * Actualiza un rol existente.
     * <p>
     * Si el nombre del rol es modificado, se verifica que el nuevo nombre no esté
     * en uso por otro registro para mantener la integridad de la base de datos.
     * </p>
     * @param id Identificador único del rol.
     * @param dto Datos actualizados.
     * @return DTO con la información actualizada.
     * @throws ResponseStatusException si el ID no existe (NOT_FOUND) o el nuevo nombre está duplicado (BAD_REQUEST).
     */
    @Override
    @Transactional
    public RolResponseDTO update(Long id, RolCreateUpdateDTO dto) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado con ID: " + id));

        if (!existente.getNombre().equalsIgnoreCase(dto.getNombre())) {
            if (rolRepository.findByNombre(dto.getNombre()).isPresent()) {
                throw new ResponseStatusException(BAD_REQUEST, "Ya existe otro rol con el nombre: " + dto.getNombre());
            }
            existente.setNombre(dto.getNombre());
        }

        Rol updatedRol = rolRepository.save(existente);
        return toRolResponseDTO(updatedRol);
    }

    /**
     * Busca un rol específico por su identificador.
     * @param id Identificador único del rol.
     * @return DTO del rol encontrado.
     * @throws ResponseStatusException si el rol no existe (NOT_FOUND).
     */
    @Override
    @Transactional(readOnly = true)
    public RolResponseDTO findById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado con ID: " + id));

        return toRolResponseDTO(rol);
    }

    /**
     * Recupera el listado completo de roles registrados.
     * @return Lista de todos los roles en formato DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> findAll() {
        return rolRepository.findAll().stream()
                .map(this::toRolResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un rol del sistema basándose en su ID.
     * <p>
     * Nota de seguridad: Se debe tener precaución al eliminar roles que puedan
     * tener usuarios asociados para evitar inconsistencias en la autenticación.
     * </p>
     * @param id Identificador del rol a eliminar.
     * @throws ResponseStatusException si el ID no existe (NOT_FOUND).
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Rol no encontrado con ID: " + id);
        }
        rolRepository.deleteById(id);
    }

    // --- Métodos de Mapeo Interno ---

    /**
     * Convierte un DTO de creación en una entidad de persistencia Rol.
     */
    private Rol toRolEntity(RolCreateUpdateDTO dto) {
        return Rol.builder().nombre(dto.getNombre()).build();
    }

    /**
     * Transforma la entidad Rol a un objeto de respuesta {@link RolResponseDTO}.
     */
    private RolResponseDTO toRolResponseDTO(Rol entity) {
        RolResponseDTO dto = new RolResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());
        return dto;
    }
}
