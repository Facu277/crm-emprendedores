package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.venta.VentaCreateUpdateDTO;
import com.emprendedores.crm.dto.venta.VentaResponseDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión del ciclo de vida de las ventas.
 * <p>
 * Proporciona la lógica para registrar transacciones, consultar historiales y 
 * filtrar ventas por cliente, manteniendo siempre el aislamiento de datos 
 * por emprendedor para garantizar la integridad del entorno multi-inquilino.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface VentaService {

    /**
     * Registra una nueva venta vinculada a un cliente y un emprendedor.
     * @param dto Datos de la transacción (monto, método de pago, etc.).
     * @param emprendedorId ID del emprendedor que realiza la venta.
     * @return DTO de la venta registrada exitosamente.
     */
    VentaResponseDTO create(VentaCreateUpdateDTO dto, Long emprendedorId);

    /**
     * Actualiza los detalles de una venta existente.
     * @param id Identificador de la venta.
     * @param dto Datos modificados.
     * @param emprendedorId ID del emprendedor para validar la propiedad del registro.
     * @return DTO con la información actualizada.
     */
    VentaResponseDTO update(Long id, VentaCreateUpdateDTO dto, Long emprendedorId);

    /**
     * Busca una venta específica validando el acceso mediante el contexto del emprendedor.
     * @param id Identificador de la venta.
     * @param emprendedorId ID del emprendedor propietario.
     * @return DTO con los detalles de la venta.
     */
    VentaResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId);

    /**
     * Recupera el historial completo de ventas registradas por un emprendedor.
     * @param emprendedorId ID del emprendedor propietario.
     * @return Lista de todas las ventas del emprendedor.
     */
    List<VentaResponseDTO> findAllByEmprendedorId(Long emprendedorId);

    /**
     * Obtiene el listado de ventas realizadas a un cliente específico.
     * <p>
     * Este método permite analizar el valor del ciclo de vida del cliente (LTV) 
     * dentro del contexto de un emprendedor.
     * </p>
     * @param clienteId ID del cliente consultado.
     * @param emprendedorId ID del emprendedor para asegurar el aislamiento.
     * @return Lista de ventas asociadas al cliente y emprendedor.
     */
    List<VentaResponseDTO> findAllByClienteIdAndEmprendedorId(Long clienteId, Long emprendedorId);

    /**
     * Elimina un registro de venta del sistema.
     * @param id Identificador de la venta.
     * @param emprendedorId ID del emprendedor para validar propiedad.
     */
    void delete(Long id, Long emprendedorId);
}

