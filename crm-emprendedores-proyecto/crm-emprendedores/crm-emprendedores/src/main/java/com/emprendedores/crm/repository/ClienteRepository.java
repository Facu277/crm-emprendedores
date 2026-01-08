package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link Cliente}.
 * <p>
 * Proporciona métodos para acceder a la información de los clientes filtrados por la 
 * relación de propiedad con un {@link com.emprendedores.crm.model.Emprendedor}.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Recupera todos los clientes que pertenecen a un emprendedor específico.
     * * @param emprendedorId ID del emprendedor propietario.
     * @return Lista de clientes asociados al emprendedor.
     */
    List<Cliente> findByEmprendedorId(Long emprendedorId);

    /**
     * Valida si un email ya está registrado para un cliente dentro de la cartera 
     * de un emprendedor específico.
     * * @param email Correo electrónico a verificar.
     * @param emprendedorId ID del emprendedor propietario.
     * @return {@code true} si el email ya existe para ese emprendedor, {@code false} de lo contrario.
     */
    boolean existsByEmailAndEmprendedorId(String email, Long emprendedorId);

    /**
     * Busca un cliente específico validando su pertenencia a un emprendedor.
     * <p>
     * Este método es fundamental para garantizar la seguridad de los datos (Multitenancy),
     * evitando que un emprendedor acceda a clientes de otros mediante el ID.
     * </p>
     * * @param id ID del cliente.
     * @param emprendedorId ID del emprendedor propietario.
     * @return Un {@link Optional} con el cliente si existe y pertenece al emprendedor.
     */
    Optional<Cliente> findByIdAndEmprendedorId(Long id, Long emprendedorId);

    /**
     * Cuenta el número total de clientes registrados por un emprendedor.
     * * @param emprendedorId ID del emprendedor propietario.
     * @return Cantidad total de clientes.
     */
    long countByEmprendedorId(Long emprendedorId);
}
