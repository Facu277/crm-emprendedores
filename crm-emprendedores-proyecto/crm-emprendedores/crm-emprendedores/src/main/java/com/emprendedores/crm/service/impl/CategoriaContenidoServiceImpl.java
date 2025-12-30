package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;
import com.emprendedores.crm.model.Emprendedor;
import com.emprendedores.crm.repository.CategoriaContenidoRepository;
import com.emprendedores.crm.repository.ContenidoRepository;
import com.emprendedores.crm.repository.EmprendedorRepository;
import com.emprendedores.crm.service.CategoriaContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.emprendedores.crm.repository.UserRepository;
import com.emprendedores.crm.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CategoriaContenidoServiceImpl implements CategoriaContenidoService {

    private final CategoriaContenidoRepository repository;
    private final UserRepository userRepository;
    private final ContenidoRepository contenidoRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaContenidoResponseDTO> findAllByUsername(String username) {
        var user = obtenerUsuario(username);
        return repository.findByEmprendedorId(user.getEmprendedor().getId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaContenidoResponseDTO findByIdAndUsername(Long id, String username) {
        var user = obtenerUsuario(username);
        return repository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(user.getEmprendedor().getId()))
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        var user = obtenerUsuario(username);

        // 1. Verificar que la categoría le pertenezca
        var cat = repository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(user.getEmprendedor().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        // 2. Verificar si tiene contenidos asociados (Opción Recomendada)

        long cantidadContenidos = contenidoRepository.countByCategoriaContenidoId(id);
        if (cantidadContenidos > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar: Esta categoría tiene " + cantidadContenidos + " contenidos asociados. Reclasifícalos primero.");
        }

        repository.delete(cat);
    }

    private User obtenerUsuario(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private CategoriaContenidoResponseDTO toResponseDTO(CategoriaContenido entity) {
        return CategoriaContenidoResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}