package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.cliente.ClienteCreateUpdateDTO;
import com.emprendedores.crm.dto.cliente.ClienteResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de la cartera de clientes de un emprendedor.
 * <p>
 * Este componente implementa un modelo de seguridad estricto donde cada operación
 * requiere que el usuario posea el rol 'EMPRENDEDOR'. El aislamiento de datos se
 * garantiza recuperando el ID del emprendedor directamente desde el contexto de 
 * seguridad de Spring, evitando manipulaciones externas.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Registra un nuevo cliente asociado al emprendedor autenticado.
     * @param user Usuario autenticado inyectado desde el contexto de seguridad.
     * @param dto Datos del cliente (nombre, contacto, etc.).
     * @return ResponseEntity con el cliente persistido y estado 201 (CREATED).
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<ClienteResponseDTO> createCliente(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ClienteCreateUpdateDTO dto) {
        return new ResponseEntity<>(clienteService.create(dto, user.getEmprendedor().getId()), HttpStatus.CREATED);
    }

    /**
     * Recupera el listado completo de clientes pertenecientes únicamente al emprendedor actual.
     * @param user Usuario autenticado.
     * @return Lista de clientes registrados por el emprendedor.
     */
    @GetMapping
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clienteService.findAllByEmprendedorId(user.getEmprendedor().getId()));
    }

    /**
     * Busca un cliente específico por su ID, validando que pertenezca a la cuenta del solicitante.
     * @param id Identificador único del cliente.
     * @param user Usuario autenticado para validación de propiedad.
     * @return DTO del cliente encontrado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<ClienteResponseDTO> getClienteById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ClienteResponseDTO cliente = clienteService.findByIdAndEmprendedorId(id, user.getEmprendedor().getId());
        return ResponseEntity.ok(cliente);
    }

    /**
     * Actualiza los datos de un cliente existente.
     * <p>El servicio verifica que el cliente realmente pertenezca al emprendedor antes de aplicar cambios.</p>
     * @param id ID del cliente a modificar.
     * @param user Usuario autenticado.
     * @param dto Nuevos datos del cliente.
     * @return Cliente actualizado con estado 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<ClienteResponseDTO> updateCliente(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ClienteCreateUpdateDTO dto) {
        ClienteResponseDTO actualizado = clienteService.update(id, dto, user.getEmprendedor().getId());
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un cliente de la base de datos de forma lógica o física según la implementación del servicio.
     * @param id ID del cliente a eliminar.
     * @param user Usuario autenticado.
     * @return Respuesta vacía con estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPRENDEDOR')")
    public ResponseEntity<Void> deleteCliente(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        clienteService.delete(id, user.getEmprendedor().getId());
        return ResponseEntity.noContent().build();
    }
}
