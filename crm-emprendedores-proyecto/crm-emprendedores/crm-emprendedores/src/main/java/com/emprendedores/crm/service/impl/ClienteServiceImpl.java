package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.cliente.ClienteCreateUpdateDTO;
import com.emprendedores.crm.dto.cliente.ClienteResponseDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorMinResponseDTO;
import com.emprendedores.crm.model.Cliente;
import com.emprendedores.crm.model.Emprendedor;
import com.emprendedores.crm.repository.ClienteRepository;
import com.emprendedores.crm.repository.EmprendedorRepository;
import com.emprendedores.crm.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor // Genera el constructor automáticamente para los campos final
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmprendedorRepository emprendedorRepository;

    @Override
    @Transactional
    public ClienteResponseDTO create(ClienteCreateUpdateDTO dto, Long emprendedorId) {
        // Obtenemos el emprendedor desde el ID del Token, no del DTO
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El emprendedor propietario no existe."));

        if (clienteRepository.existsByEmailAndEmprendedorId(dto.getEmail(), emprendedorId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe un cliente con ese email registrado en tu cuenta.");
        }

        Cliente cliente = toClienteEntity(dto, emprendedor);
        Cliente savedCliente = clienteRepository.save(cliente);

        return toClienteResponseDTO(savedCliente, emprendedor);
    }

    @Override
    @Transactional
    public ClienteResponseDTO update(Long id, ClienteCreateUpdateDTO dto, Long emprendedorId) {
        // Buscamos directamente por ID de cliente y ID de dueño (Seguridad Multi-tenant)
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(id, emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado o no tienes permisos."));

        updateClienteEntity(cliente, dto);

        Cliente updatedCliente = clienteRepository.save(cliente);
        return toClienteResponseDTO(updatedCliente, updatedCliente.getEmprendedor());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId) {
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(id, emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado en tu cuenta."));

        return toClienteResponseDTO(cliente, cliente.getEmprendedor());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAllByEmprendedorId(Long emprendedorId) {
        return clienteRepository.findByEmprendedorId(emprendedorId).stream()
                .map(c -> toClienteResponseDTO(c, c.getEmprendedor()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id, Long emprendedorId) {
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(id, emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No se pudo eliminar: Cliente no encontrado."));

        clienteRepository.delete(cliente);
    }

    // --- Métodos de Mapeo Manual ---
    private Cliente toClienteEntity(ClienteCreateUpdateDTO dto, Emprendedor emprendedor) {
        return Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .etiqueta(dto.getEtiqueta())
                .emprendedor(emprendedor)
                .build();
    }

    private void updateClienteEntity(Cliente entity, ClienteCreateUpdateDTO dto) {
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setEtiqueta(dto.getEtiqueta());
    }

    private ClienteResponseDTO toClienteResponseDTO(Cliente entity, Emprendedor emprendedor) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setEtiqueta(entity.getEtiqueta());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());
        dto.setEmprendedor(toEmprendedorMinResponseDTO(emprendedor));
        return dto;
    }

    private EmprendedorMinResponseDTO toEmprendedorMinResponseDTO(Emprendedor entity) {
        EmprendedorMinResponseDTO dto = new EmprendedorMinResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setRubro(entity.getRubro());
        return dto;
    }
}
