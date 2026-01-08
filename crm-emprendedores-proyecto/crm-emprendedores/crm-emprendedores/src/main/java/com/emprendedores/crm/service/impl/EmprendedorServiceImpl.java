package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.emprendedor.EmprendedorCreateUpdateDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import com.emprendedores.crm.model.Emprendedor;
import com.emprendedores.crm.model.Rol;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.repository.EmprendedorRepository;
import com.emprendedores.crm.repository.RolRepository;
import com.emprendedores.crm.repository.UserRepository;
import com.emprendedores.crm.service.EmprendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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

/**
 * Implementación del servicio para la gestión integral de emprendedores.
 * <p>
 * Esta clase maneja el flujo de registro de nuevos usuarios, la persistencia de perfiles
 * y el almacenamiento de recursos multimedia (fotos de perfil). Implementa una lógica
 * de negocio de doble persistencia: crea el perfil del emprendedor y, simultáneamente,
 * genera el usuario de acceso con contraseñas encriptadas.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class EmprendedorServiceImpl implements EmprendedorService {

    private final EmprendedorRepository emprendedorRepository;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Ruta del directorio donde se almacenan físicamente las fotos de perfil.
     */
    private final String UPLOAD_DIR = "uploads/emprendedores/";

    /**
     * Registra un nuevo emprendedor y su cuenta de acceso al sistema.
     * <p>
     * El proceso incluye:
     * 1. Validación de email único.
     * 2. Almacenamiento de foto de perfil en disco.
     * 3. Creación de la entidad Emprendedor.
     * 4. Creación de la entidad User vinculada con el rol "EMPRENDEDOR".
     * </p>
     * @param dto Datos del perfil y credenciales iniciales.
     * @param fotoPerfil Archivo de imagen para el perfil.
     * @return DTO del emprendedor creado.
     * @throws ResponseStatusException si el email ya existe (BAD_REQUEST) o el rol no está configurado (INTERNAL_SERVER_ERROR).
     */
    @Override
    @Transactional
    public EmprendedorResponseDTO create(EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil) {
        if (emprendedorRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un emprendedor con este email.");
        }

        Emprendedor emprendedor = toEmprendedorEntity(dto);
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            emprendedor.setFotoPerfil(savePhoto(fotoPerfil));
        }
        Emprendedor savedEmprendedor = emprendedorRepository.save(emprendedor);

        Rol rol = rolRepository.findByNombre("EMPRENDEDOR")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El rol EMPRENDEDOR no existe en la base de datos."));

        User user = User.builder()
                .username(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .emprendedor(savedEmprendedor)
                .rol(rol)
                .build();

        userRepository.save(user);

        return toEmprendedorResponseDTO(savedEmprendedor);
    }

    /**
     * Actualiza la información del perfil del emprendedor.
     * @param id Identificador único del emprendedor.
     * @param dto Datos actualizados.
     * @param fotoPerfil Nueva foto de perfil (reemplaza la anterior si se provee).
     * @return DTO con la información actualizada.
     */
    @Override
    @Transactional
    public EmprendedorResponseDTO update(Long id, EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil) {
        Emprendedor emprendedor = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendedor no encontrado"));

        updateEmprendedorEntity(emprendedor, dto);

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            emprendedor.setFotoPerfil(savePhoto(fotoPerfil));
        }

        return toEmprendedorResponseDTO(emprendedorRepository.save(emprendedor));
    }

    /**
     * Busca un emprendedor por su ID.
     * @param id Identificador único.
     * @return DTO de respuesta.
     */
    @Override
    @Transactional(readOnly = true)
    public EmprendedorResponseDTO findById(Long id) {
        return emprendedorRepository.findById(id)
                .map(this::toEmprendedorResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el emprendedor"));
    }

    /**
     * Obtiene todos los emprendedores registrados en la plataforma.
     * @return Lista de DTOs de emprendedores.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmprendedorResponseDTO> findAll() {
        return emprendedorRepository.findAll().stream()
                .map(this::toEmprendedorResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina el registro de un emprendedor.
     * <p>Nota: La eliminación del usuario vinculado depende de la configuración de cascada en la entidad.</p>
     * @param id ID del emprendedor a eliminar.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Emprendedor emp = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontrado"));

        emprendedorRepository.delete(emp);
    }

    /**
     * Guarda físicamente la foto en el servidor y genera un nombre de archivo único.
     * @param file Archivo recibido del cliente.
     * @return Nombre del archivo generado.
     */
    private String savePhoto(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar foto");
        }
    }

    // --- Métodos de Mapeo Interno ---

    private Emprendedor toEmprendedorEntity(EmprendedorCreateUpdateDTO dto) {
        return Emprendedor.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .rubro(dto.getRubro())
                .pais(dto.getPais())
                .descripcion(dto.getDescripcion())
                .build();
    }

    private void updateEmprendedorEntity(Emprendedor entity, EmprendedorCreateUpdateDTO dto) {
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setTelefono(dto.getTelefono());
        entity.setRubro(dto.getRubro());
        entity.setPais(dto.getPais());
        entity.setDescripcion(dto.getDescripcion());
    }

    private EmprendedorResponseDTO toEmprendedorResponseDTO(Emprendedor entity) {
        EmprendedorResponseDTO dto = new EmprendedorResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setRubro(entity.getRubro());
        dto.setPais(entity.getPais());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFotoPerfil(entity.getFotoPerfil());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());
        return dto;
    }
}