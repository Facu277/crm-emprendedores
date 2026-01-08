package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.cliente.ClienteCreateUpdateDTO;
import com.emprendedores.crm.dto.cliente.ClienteResponseDTO;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de clientes dentro del CRM.
 * <p>
 * Implementa lógica de aislamiento de datos asegurando que las operaciones 
 * sobre clientes siempre estén restringidas al ID del emprendedor autenticado.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface ClienteService {

    /**
     * Registra un nuevo cliente bajo la cartera de un emprendedor.
     * @param dto Datos del cliente.
     * @param emprendedorId ID del emprendedor propietario.
     * @return DTO del cliente creado.
     */
    ClienteResponseDTO create(ClienteCreateUpdateDTO dto, Long emprendedorId);

    /**
     * Actualiza la información de un cliente validando su pertenencia al emprendedor.
     * @param id Identificador del cliente.
     * @param dto Datos actualizados.
     * @param emprendedorId ID del emprendedor propietario.
     * @return DTO actualizado.
     */
    ClienteResponseDTO update(Long id, ClienteCreateUpdateDTO dto, Long emprendedorId);

    /**
     * Recupera un cliente específico validando el acceso mediante el ID del emprendedor.
     * @param id Identificador del cliente.
     * @param emprendedorId ID del emprendedor propietario.
     * @return DTO con la información del cliente.
     */
    ClienteResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId);

    /**
     * Obtiene el listado completo de clientes asociados a un emprendedor.
     * @param emprendedorId ID del emprendedor propietario.
     * @return Lista de clientes.
     */
    List<ClienteResponseDTO> findAllByEmprendedorId(Long emprendedorId);

    /**
     * Elimina un cliente del sistema, validando la propiedad del registro.
     * @param id Identificador del cliente.
     * @param emprendedorId ID del emprendedor propietario.
     */
    void delete(Long id, Long emprendedorId);
}