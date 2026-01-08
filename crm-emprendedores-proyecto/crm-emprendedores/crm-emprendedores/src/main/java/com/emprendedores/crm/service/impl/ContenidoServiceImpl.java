package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoMinResponseDTO;
import com.emprendedores.crm.dto.contenido.ContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.contenido.ContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;
import com.emprendedores.crm.model.Contenido;
import com.emprendedores.crm.model.Emprendedor;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Implementación del servicio para la gestión de contenidos multimedia.
 * <p>
 * Esta clase administra el ciclo de vida de las publicaciones, incluyendo la carga
 * y eliminación de archivos físicos en el servidor, asegurando que cada emprendedor
 * solo pueda manipular contenidos de su propiedad y categorías autorizadas.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ContenidoServiceImpl implements ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final CategoriaContenidoRepository categoriaContenidoRepository;

    /**
     * Directorio raíz para el almacenamiento de archivos de contenido.
     */
    private final String UPLOAD_DIR = "uploads/contenidos/";

    /**
     * Crea un nuevo registro de contenido y almacena su imagen adjunta.
     * @param dto Datos del contenido.
     * @param emprendedorId ID del emprendedor propietario.
     * @param imagen Archivo multimedia proporcionado por el usuario.
     * @return DTO del contenido creado con la URL/nombre de la imagen procesada.
     * @throws ResponseStatusException si la categoría no pertenece al emprendedor.
     */
    @Override
    @Transactional
    public ContenidoResponseDTO create(ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen) {
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Emprendedor no encontrado."));

        CategoriaContenido categoria = categoriaContenidoRepository.findById(dto.getCategoriaContenidoId())
                .filter(cat -> cat.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La categoría no existe o no te pertenece."));

        Contenido contenido = toContenidoEntity(dto, emprendedor, categoria);

        if (imagen != null && !imagen.isEmpty()) {
            contenido.setImagen(saveImage(imagen, emprendedorId));
        }

        return toContenidoResponseDTO(contenidoRepository.save(contenido));
    }

    /**
     * Actualiza un contenido existente, gestionando la sustitución de archivos físicos si es necesario.
     * @param id ID del contenido a modificar.
     * @param dto Datos actualizados.
     * @param emprendedorId ID para validación de propiedad.
     * @param imagen Nueva imagen (opcional).
     * @return DTO con los datos actualizados.
     */
    @Override
    @Transactional
    public ContenidoResponseDTO update(Long id, ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen) {
        Contenido contenido = contenidoRepository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Contenido no encontrado en tu cuenta."));

        if (!contenido.getCategoriaContenido().getId().equals(dto.getCategoriaContenidoId())) {
            CategoriaContenido nuevaCat = categoriaContenidoRepository.findById(dto.getCategoriaContenidoId())
                    .filter(cat -> cat.getEmprendedor().getId().equals(emprendedorId))
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La nueva categoría es inválida o no te pertenece."));
            contenido.setCategoriaContenido(nuevaCat);
        }

        updateContenidoEntity(contenido, dto);

        if (imagen != null && !imagen.isEmpty()) {
            if (contenido.getImagen() != null) {
                deleteImageFile(contenido.getImagen());
            }
            contenido.setImagen(saveImage(imagen, emprendedorId));
        }

        return toContenidoResponseDTO(contenidoRepository.save(contenido));
    }

    /**
     * Busca un contenido por ID garantizando el aislamiento entre emprendedores.
     * @param id Identificador único del contenido.
     * @param emprendedorId ID del dueño.
     * @return DTO del contenido.
     */
    @Override
    @Transactional(readOnly = true)
    public ContenidoResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId) {
        return contenidoRepository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(emprendedorId))
                .map(this::toContenidoResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Contenido no encontrado."));
    }

    /**
     * Recupera todos los contenidos asociados a un emprendedor.
     * @param emprendedorId ID del dueño.
     * @return Lista de contenidos en formato DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContenidoResponseDTO> findAllByEmprendedorId(Long emprendedorId) {
        return contenidoRepository.findByEmprendedorId(emprendedorId).stream()
                .map(this::toContenidoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un contenido de la base de datos y su archivo correspondiente en el sistema de archivos.
     * @param id ID del contenido.
     * @param emprendedorId ID del dueño para validación.
     */
    @Override
    @Transactional
    public void delete(Long id, Long emprendedorId) {
        Contenido contenido = contenidoRepository.findById(id)
                .filter(c -> c.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No se encontró el contenido para eliminar."));

        if (contenido.getImagen() != null) {
            deleteImageFile(contenido.getImagen());
        }

        contenidoRepository.delete(contenido);
    }

    /**
     * Guarda físicamente la imagen en el servidor con un nombre único para evitar colisiones.
     * @param file Archivo recibido.
     * @param emprendedorId ID del emprendedor para prefijo de archivo.
     * @return Nombre final del archivo guardado.
     */
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

    /**
     * Elimina un archivo del disco de forma segura.
     * <p>Si falla la eliminación física, se registra el error pero no se revierte la transacción de la DB.</p>
     * @param fileName Nombre del archivo a borrar.
     */
    private void deleteImageFile(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error al intentar eliminar el archivo " + fileName + ": " + e.getMessage());
        }
    }

    // --- Métodos de Mapeo Interno ---

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

        if (entity.getCategoriaContenido() != null) {
            dto.setCategoriaContenido(CategoriaContenidoMinResponseDTO.builder()
                    .id(entity.getCategoriaContenido().getId())
                    .nombre(entity.getCategoriaContenido().getNombre())
                    .build());
        }

        return dto;
    }
}