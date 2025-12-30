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

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    // ... (Mappers simulados)

    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public RolResponseDTO create(RolCreateUpdateDTO dto) {
        // 1. Validar unicidad
        if (rolRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe un rol con el nombre: " + dto.getNombre());
        }

        Rol rol = toRolEntity(dto); // Mapeo
        Rol savedRol = rolRepository.save(rol);

        return toRolResponseDTO(savedRol); // Mapeo
    }

    @Override
    @Transactional
    public RolResponseDTO update(Long id, RolCreateUpdateDTO dto) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado con ID: " + id));

        // 1. Validar unicidad si el nombre cambia
        if (!existente.getNombre().equalsIgnoreCase(dto.getNombre())) {
            if (rolRepository.findByNombre(dto.getNombre()).isPresent()) {
                throw new ResponseStatusException(BAD_REQUEST, "Ya existe otro rol con el nombre: " + dto.getNombre());
            }
            existente.setNombre(dto.getNombre());
        }

        Rol updatedRol = rolRepository.save(existente);
        return toRolResponseDTO(updatedRol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponseDTO findById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado con ID: " + id));

        return toRolResponseDTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> findAll() {
        return rolRepository.findAll().stream()
                .map(this::toRolResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Rol no encontrado con ID: " + id);
        }
        // Nota: En un sistema real, se debe validar que ningún usuario esté asignado a este rol.
        rolRepository.deleteById(id);
    }

    // --- Métodos de Simulación (Reemplazar con MapStruct) ---
    private Rol toRolEntity(RolCreateUpdateDTO dto) {
        return Rol.builder().nombre(dto.getNombre()).build();
    }

    private RolResponseDTO toRolResponseDTO(Rol entity) {
        RolResponseDTO dto = new RolResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());
        return dto;
    }
}
