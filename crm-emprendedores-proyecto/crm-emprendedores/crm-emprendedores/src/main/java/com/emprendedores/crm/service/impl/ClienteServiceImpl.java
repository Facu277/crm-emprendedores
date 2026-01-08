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

/**
 * Implementación del servicio para la gestión de clientes con arquitectura Multi-tenant.
 * <p>
 * Asegura que todas las operaciones (lectura, escritura y borrado) estén filtradas por el
 * ID del emprendedor propietario para garantizar la privacidad y seguridad de los datos.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmprendedorRepository emprendedorRepository;

    /**
     * Crea un nuevo cliente vinculado al emprendedor autenticado.
     * @param dto Datos personales del cliente.
     * @param emprendedorId ID del emprendedor obtenido del contexto de seguridad.
     * @return DTO del cliente guardado.
     * @throws ResponseStatusException si el email ya existe para ese emprendedor (BAD_REQUEST).
     */
    @Override
    @Transactional
    public ClienteResponseDTO create(ClienteCreateUpdateDTO dto, Long emprendedorId) {
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El emprendedor propietario no existe."));

        if (clienteRepository.existsByEmailAndEmprendedorId(dto.getEmail(), emprendedorId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe un cliente con ese email registrado en tu cuenta.");
        }

        Cliente cliente = toClienteEntity(dto, emprendedor);
        Cliente savedCliente = clienteRepository.save(cliente);

        return toClienteResponseDTO(savedCliente, emprendedor);
    }

    /**
     * Actualiza la información de un cliente siempre que pertenezca al emprendedor solicitante.
     * @param id ID del cliente a modificar.
     * @param dto Datos actualizados.
     * @param emprendedorId ID del dueño para validación de seguridad.
     * @return DTO actualizado.
     */
    @Override
    @Transactional
    public ClienteResponseDTO update(Long id, ClienteCreateUpdateDTO dto, Long emprendedorId) {
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(id, emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado o no tienes permisos."));

        updateClienteEntity(cliente, dto);

        Cliente updatedCliente = clienteRepository.save(cliente);
        return toClienteResponseDTO(updatedCliente, updatedCliente.getEmprendedor());
    }

    /**
     * Recupera un cliente por ID validando la pertenencia al emprendedor.
     * @param id ID del cliente.
     * @param emprendedorId ID del dueño.
     * @return DTO del cliente.
     */
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId) {
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(id, emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado en tu cuenta."));

        return toClienteResponseDTO(cliente, cliente.getEmprendedor());
    }

    /**
     * Lista todos los clientes pertenecientes a un emprendedor.
     * @param emprendedorId ID del emprendedor.
     * @return Lista de clientes asociados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAllByEmprendedorId(Long emprendedorId) {
        return clienteRepository.findByEmprendedorId(emprendedorId).stream()
                .map(c -> toClienteResponseDTO(c, c.getEmprendedor()))
                .collect(Collectors.toList());
    }

    /**
     * Elimina un cliente validando la propiedad del registro antes de proceder.
     * @param id ID del cliente.
     * @param emprendedorId ID del dueño.
     */
    @Override
    @Transactional
    public void delete(Long id, Long emprendedorId) {
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(id, emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No se pudo eliminar: Cliente no encontrado."));

        clienteRepository.delete(cliente);
    }

    /**
     * Convierte un DTO de creación en una entidad Cliente.
     */
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

    /**
     * Actualiza los campos de una entidad existente con los datos del DTO.
     */
    private void updateClienteEntity(Cliente entity, ClienteCreateUpdateDTO dto) {
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setEtiqueta(dto.getEtiqueta());
    }

    /**
     * Convierte la entidad y su relación con Emprendedor a un DTO de respuesta detallado.
     */
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

    /**
     * Crea una representación mínima del emprendedor para incluir en la respuesta del cliente.
     */
    private EmprendedorMinResponseDTO toEmprendedorMinResponseDTO(Emprendedor entity) {
        EmprendedorMinResponseDTO dto = new EmprendedorMinResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setRubro(entity.getRubro());
        return dto;
    }
}
