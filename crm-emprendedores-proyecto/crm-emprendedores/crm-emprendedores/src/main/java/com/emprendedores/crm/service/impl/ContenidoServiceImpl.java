package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.cliente.ClienteResponseDTO;
import com.emprendedores.crm.dto.contenido.ContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.contenido.ContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;
import com.emprendedores.crm.model.Contenido;
import com.emprendedores.crm.model.Emprendedor;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoMinResponseDTO;
import com.emprendedores.crm.repository.CategoriaContenidoRepository;
import com.emprendedores.crm.repository.ContenidoRepository;
import com.emprendedores.crm.repository.EmprendedorRepository;
import com.emprendedores.crm.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ContenidoServiceImpl implements ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final CategoriaContenidoRepository categoriaContenidoRepository;

    private final String UPLOAD_DIR = "uploads/contenidos/";

    @Override
    @Transactional
    public ContenidoResponseDTO create(ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen) {
        // 1. Obtener Emprendedor del Token
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Emprendedor no encontrado."));

        // 2. Validar que la categoría pertenezca al mismo emprendedor (Aislamiento)
        CategoriaContenido categoria = categoriaContenidoRepository.findById(dto.getCategoriaContenidoId())
                .filter(cat -> cat.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La categoría no existe o no te pertenece."));

        Contenido contenido = toContenidoEntity(dto, emprendedor, categoria);

        // 3. Manejo de imagen
        if (imagen != null && !imagen.isEmpty()) {
            contenido.setImagen(saveImage(imagen, emprendedorId));
        }

        return toContenidoResponseDTO(contenidoRepository.save(contenido));
    }

    @Override
    @Transactional
    public ContenidoResponseDTO update(Long id, ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen) {
        // 1. Buscar contenido asegurando que pertenece al emprendedor
        Contenido contenido = contenidoRepository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Contenido no encontrado en tu cuenta."));

        // 2. Validar nueva categoría si cambió
        if (!contenido.getCategoriaContenido().getId().equals(dto.getCategoriaContenidoId())) {
            CategoriaContenido nuevaCat = categoriaContenidoRepository.findById(dto.getCategoriaContenidoId())
                    .filter(cat -> cat.getEmprendedor().getId().equals(emprendedorId))
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La nueva categoría es inválida o no te pertenece."));
            contenido.setCategoriaContenido(nuevaCat);
        }

        // 3. Actualizar campos básicos
        updateContenidoEntity(contenido, dto);

        // 4. Manejo de imagen (solo si se envía una nueva)
        if (imagen != null && !imagen.isEmpty()) {
            // Borramos la imagen anterior del disco para no dejar basura
            if (contenido.getImagen() != null) {
                deleteImageFile(contenido.getImagen());
            }
            // Guardamos la nueva imagen y actualizamos el nombre en la entidad
            contenido.setImagen(saveImage(imagen, emprendedorId));
        }

        return toContenidoResponseDTO(contenidoRepository.save(contenido));
    }

    @Override
    @Transactional(readOnly = true)
    public ContenidoResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId) {
        return contenidoRepository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(emprendedorId))
                .map(this::toContenidoResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Contenido no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContenidoResponseDTO> findAllByEmprendedorId(Long emprendedorId) {
        return contenidoRepository.findByEmprendedorId(emprendedorId).stream()
                .map(this::toContenidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id, Long emprendedorId) {
        // 1. Buscar contenido asegurando propiedad
        Contenido contenido = contenidoRepository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No se encontró el contenido para eliminar."));

        // 2. Borrar el archivo físico del disco si existe
        if (contenido.getImagen() != null) {
            deleteImageFile(contenido.getImagen());
        }

        // 3. Eliminar el registro de la base de datos
        contenidoRepository.delete(contenido);
    }

    // --- Mapeos manuales ---

    private Contenido toContenidoEntity(ContenidoCreateUpdateDTO dto, Emprendedor emp, CategoriaContenido cat) {
        return Contenido.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado())
                .fechaProgramada(dto.getFechaProgramada())
                .emprendedor(emp)
                .categoriaContenido(cat)
                .build();
    }

    private void updateContenidoEntity(Contenido entity, ContenidoCreateUpdateDTO dto) {
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEstado(dto.getEstado());
        entity.setFechaProgramada(dto.getFechaProgramada());
    }

    private ContenidoResponseDTO toContenidoResponseDTO(Contenido entity) {
        ContenidoResponseDTO dto = new ContenidoResponseDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setEstado(entity.getEstado());
        dto.setImagen(entity.getImagen());
        dto.setFechaProgramada(entity.getFechaProgramada());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());

        // --- ESTO ES LO QUE FALTABA ---
        if (entity.getCategoriaContenido() != null) {
            dto.setCategoriaContenido(CategoriaContenidoMinResponseDTO.builder()
                    .id(entity.getCategoriaContenido().getId())
                    .nombre(entity.getCategoriaContenido().getNombre())
                    .build());
        }

        return dto;
    }

    private String saveImage(MultipartFile file, Long emprendedorId) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "emp_" + emprendedorId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen");
        }
    }

    private void deleteImageFile(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            boolean eliminado = Files.deleteIfExists(filePath);
            if (eliminado) {
                System.out.println("Archivo eliminado físicamente: " + fileName);
            }
        } catch (IOException e) {
            // Logeamos el error pero no interrumpimos la transacción de la DB
            System.err.println("Error al intentar eliminar el archivo " + fileName + ": " + e.getMessage());
        }
    }
}