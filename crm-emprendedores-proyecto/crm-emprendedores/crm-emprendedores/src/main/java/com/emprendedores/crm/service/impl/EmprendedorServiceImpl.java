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
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

// *Simulación de Mapper y StorageService*
// Asumimos que existen clases de utilidad para el mapeo y manejo de archivos.

@Service
@RequiredArgsConstructor // Genera el constructor con los campos 'final' automáticamente
public class EmprendedorServiceImpl implements EmprendedorService {

    private final EmprendedorRepository emprendedorRepository;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    private final String UPLOAD_DIR = "uploads/emprendedores/";

    @Override
    @Transactional
    public EmprendedorResponseDTO create(EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil) {
        // 1. Validar si el email ya existe
        if (emprendedorRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un emprendedor con este email.");
        }

        // 2. Crear y guardar la entidad Emprendedor
        Emprendedor emprendedor = toEmprendedorEntity(dto);
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            emprendedor.setFotoPerfil(savePhoto(fotoPerfil));
        }
        Emprendedor savedEmprendedor = emprendedorRepository.save(emprendedor);

        // 3. Buscar el Rol EMPRENDEDOR
        Rol rol = rolRepository.findByNombre("EMPRENDEDOR")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El rol EMPRENDEDOR no existe en la base de datos."));

        // 4. Crear el Usuario de acceso vinculado
        User user = User.builder()
                .username(dto.getEmail()) // Su email será su usuario de login
                .password(passwordEncoder.encode(dto.getPassword())) // Encriptar contraseña
                .emprendedor(savedEmprendedor)
                .rol(rol)
                .build();

        userRepository.save(user);

        return toEmprendedorResponseDTO(savedEmprendedor);
    }

    @Override
    @Transactional
    public EmprendedorResponseDTO update(Long id, EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil) {
        Emprendedor emprendedor = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendedor no encontrado"));

        updateEmprendedorEntity(emprendedor, dto);

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            // Opcional: Borrar foto anterior aquí antes de asignar la nueva
            emprendedor.setFotoPerfil(savePhoto(fotoPerfil));
        }

        return toEmprendedorResponseDTO(emprendedorRepository.save(emprendedor));
    }

    @Override
    @Transactional(readOnly = true)
    public EmprendedorResponseDTO findById(Long id) {
        return emprendedorRepository.findById(id)
                .map(this::toEmprendedorResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el emprendedor"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmprendedorResponseDTO> findAll() {
        return emprendedorRepository.findAll().stream()
                .map(this::toEmprendedorResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Emprendedor emp = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontrado"));

        // Al borrar el emprendedor, se borrarán sus usuarios si pusiste CascadeType.ALL
        emprendedorRepository.delete(emp);
    }

    // --- Mapeos y Utilidades ---

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
}