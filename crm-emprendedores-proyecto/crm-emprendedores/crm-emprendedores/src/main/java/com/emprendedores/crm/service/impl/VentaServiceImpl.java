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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Implementación del servicio para la gestión de transacciones comerciales (Ventas).
 * <p>
 * Gestiona el registro de ingresos y el historial de ventas por cliente, manteniendo
 * el aislamiento multi-tenant. La clase asegura que los datos del cliente se adjunten
 * correctamente a la respuesta para facilitar la visualización en el frontend.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final ClienteRepository clienteRepository;

    /**
     * Registra una nueva venta vinculada a un cliente y un emprendedor.
     * <p>
     * Se valida explícitamente que el cliente pertenezca al emprendedor que realiza la operación
     * antes de persistir el registro.
     * </p>
     * @param dto Datos de la venta (monto, método de pago, etc.).
     * @param emprendedorId ID del emprendedor autenticado.
     * @return DTO de la venta con la información del cliente anidada.
     * @throws ResponseStatusException si el cliente no pertenece al emprendedor (BAD_REQUEST).
     */
    @Override
    @Transactional
    public VentaResponseDTO create(VentaCreateUpdateDTO dto, Long emprendedorId) {
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Emprendedor no encontrado."));

        Cliente cliente = clienteRepository.findByIdAndEmprendedorId(dto.getClienteId(), emprendedorId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "El cliente no existe o no pertenece a tu cuenta."));

        Venta venta = toVentaEntity(dto, emprendedor, cliente);
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Se asegura que la relación esté cargada para el mapeo de respuesta
        ventaGuardada.setCliente(cliente);

        return toVentaResponseDTO(ventaGuardada);
    }

    /**
     * Actualiza los detalles de una venta existente.
     * @param id Identificador de la venta.
     * @param dto Datos actualizados.
     * @param emprendedorId ID del dueño para validación de permisos.
     * @return DTO con los cambios aplicados.
     */
    @Override
    @Transactional
    public VentaResponseDTO update(Long id, VentaCreateUpdateDTO dto, Long emprendedorId) {
        Venta venta = ventaRepository.findById(id)
                .filter(v -> v.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada."));

        updateVentaEntity(venta, dto);
        return toVentaResponseDTO(ventaRepository.save(venta));
    }

    /**
     * Recupera una venta por su ID, validando la propiedad de la misma.
     * @param id ID de la venta.
     * @param emprendedorId ID del dueño.
     * @return DTO de la venta encontrada.
     */
    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId) {
        return ventaRepository.findById(id)
                .filter(v -> v.getEmprendedor().getId().equals(emprendedorId))
                .map(this::toVentaResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada."));
    }

    /**
     * Obtiene el historial completo de ventas de un emprendedor.
     * @param emprendedorId ID del dueño.
     * @return Lista de ventas registradas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> findAllByEmprendedorId(Long emprendedorId) {
        return ventaRepository.findByEmprendedorId(emprendedorId).stream()
                .map(this::toVentaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera todas las ventas asociadas a un cliente específico de un emprendedor.
     * @param clienteId ID del cliente.
     * @param emprendedorId ID del dueño.
     * @return Lista de ventas del cliente.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> findAllByClienteIdAndEmprendedorId(Long clienteId, Long emprendedorId) {
        return ventaRepository.findByClienteIdAndEmprendedorId(clienteId, emprendedorId).stream()
                .map(this::toVentaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina el registro de una venta.
     * @param id ID de la venta a borrar.
     * @param emprendedorId ID para validar la propiedad.
     */
    @Override
    @Transactional
    public void delete(Long id, Long emprendedorId) {
        Venta venta = ventaRepository.findById(id)
                .filter(v -> v.getEmprendedor().getId().equals(emprendedorId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada."));
        ventaRepository.delete(venta);
    }

    // --- Métodos de Mapeo Interno ---

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
                .cliente(ClienteMinResponseDTO.builder()
                        .id(entity.getCliente().getId())
                        .nombre(entity.getCliente().getNombre())
                        .apellido(entity.getCliente().getApellido())
                        .build())
                .build();
    }
}