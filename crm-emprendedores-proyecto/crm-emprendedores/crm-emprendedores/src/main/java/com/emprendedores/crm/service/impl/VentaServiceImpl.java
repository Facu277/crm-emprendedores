package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.venta.VentaCreateUpdateDTO;
import com.emprendedores.crm.dto.venta.VentaResponseDTO;
import com.emprendedores.crm.dto.cliente.ClienteMinResponseDTO;
import com.emprendedores.crm.model.Cliente;
import com.emprendedores.crm.model.Emprendedor;
import com.emprendedores.crm.model.Venta;
import com.emprendedores.crm.repository.ClienteRepository;
import com.emprendedores.crm.repository.EmprendedorRepository;
import com.emprendedores.crm.repository.VentaRepository;
import com.emprendedores.crm.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public VentaResponseDTO create(VentaCreateUpdateDTO dto, Long emprendedorId) {
        // 1. Obtener Emprendedor
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Emprendedor no encontrado."));

        // 2. Obtener Cliente (Ya lo tienes aquí en memoria)
        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(dto.getClienteId(), emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "El cliente no existe o no pertenece a tu cuenta."));

        // 3. Crear la entidad
        Venta venta = toVentaEntity(dto, emprendedor, cliente);

        // 4. Guardar
        Venta ventaGuardada = ventaRepository.save(venta);

        // --- EL CAMBIO CLAVE AQUÍ ---
        // Le asignamos el objeto 'cliente' que ya tenemos cargado con nombre y apellido
        // para que el mapeador pueda leer esos datos.
        ventaGuardada.setCliente(cliente);

        return toVentaResponseDTO(ventaGuardada);
    }

    @Override
    @Transactional
    public VentaResponseDTO update(Long id, VentaCreateUpdateDTO dto, Long emprendedorId) {
        Venta venta = ventaRepository.findById(id)
                .filter(v -> v.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada."));

        updateVentaEntity(venta, dto);
        return toVentaResponseDTO(ventaRepository.save(venta));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId) {
        return ventaRepository.findById(id)
                .filter(v -> v.getEmprendedor().getId().equals(emprendedorId))
                .map(this::toVentaResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> findAllByEmprendedorId(Long emprendedorId) {
        return ventaRepository.findByEmprendedorId(emprendedorId).stream()
                .map(this::toVentaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> findAllByClienteIdAndEmprendedorId(Long clienteId, Long emprendedorId) {
        return ventaRepository.findByClienteIdAndEmprendedorId(clienteId, emprendedorId).stream()
                .map(this::toVentaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id, Long emprendedorId) {
        Venta venta = ventaRepository.findById(id)
                .filter(v -> v.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada."));
        ventaRepository.delete(venta);
    }

    // --- Mapeos ---
    private Venta toVentaEntity(VentaCreateUpdateDTO dto, Emprendedor emp, Cliente cli) {
        return Venta.builder()
                .monto(dto.getMonto())
                .estado(dto.getEstado())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .metodoPago(dto.getMetodoPago())
                .descripcion(dto.getDescripcion())
                .emprendedor(emp)
                .cliente(cli)
                .build();
    }

    private void updateVentaEntity(Venta entity, VentaCreateUpdateDTO dto) {
        entity.setMonto(dto.getMonto());
        entity.setEstado(dto.getEstado());
        entity.setMetodoPago(dto.getMetodoPago());
        entity.setDescripcion(dto.getDescripcion());
        // AÑADE ESTA LÍNEA:
        entity.setFecha(dto.getFecha());
    }

    private VentaResponseDTO toVentaResponseDTO(Venta entity) {
        return VentaResponseDTO.builder()
                .id(entity.getId())
                .monto(entity.getMonto())
                .estado(entity.getEstado())
                .fecha(entity.getFecha())
                .metodoPago(entity.getMetodoPago())
                .descripcion(entity.getDescripcion())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                // Mapeamos el cliente de forma minimalista
                .cliente(ClienteMinResponseDTO.builder()
                        .id(entity.getCliente().getId())
                        .nombre(entity.getCliente().getNombre())
                        .apellido(entity.getCliente().getApellido())
                        .build())
                .build();
    }
}