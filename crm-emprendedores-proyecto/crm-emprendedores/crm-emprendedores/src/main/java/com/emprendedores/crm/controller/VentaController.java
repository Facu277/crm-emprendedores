package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.venta.VentaCreateUpdateDTO;
import com.emprendedores.crm.dto.venta.VentaResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de registros de ventas y transacciones comerciales.
 * <p>
 * Este controlador expone los endpoints necesarios para el seguimiento financiero de los 
 * emprendedores. Implementa un modelo de aislamiento de datos donde cada usuario solo 
 * puede interactuar con las ventas vinculadas a su propio perfil de emprendedor.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    /**
     * Registra una nueva venta en el sistema.
     * <p>
     * El sistema asocia automáticamente la venta al emprendedor autenticado,
     * garantizando la integridad de los datos financieros.
     * </p>
     * @param user Usuario autenticado (proporcionado por Spring Security).
     * @param dto Datos de la transacción (monto, cliente, método de pago, etc.).
     * @return ResponseEntity con la venta creada y estado 201 (CREATED).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<VentaResponseDTO> createVenta(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid VentaCreateUpdateDTO dto) {

        VentaResponseDTO newVenta = ventaService.create(dto, user.getEmprendedor().getId());
        return new ResponseEntity<>(newVenta, HttpStatus.CREATED);
    }

    /**
     * Actualiza la información de una venta existente.
     * @param id Identificador único de la venta.
     * @param user Usuario autenticado para validar la propiedad del registro.
     * @param dto Datos actualizados de la venta.
     * @return Venta actualizada con estado 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<VentaResponseDTO> updateVenta(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid VentaCreateUpdateDTO dto) {

        VentaResponseDTO updatedVenta = ventaService.update(id, dto, user.getEmprendedor().getId());
        return ResponseEntity.ok(updatedVenta);
    }

    /**
     * Recupera el detalle de una venta específica.
     * @param id ID de la venta.
     * @param user Usuario autenticado.
     * @return DTO de la venta solicitada.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<VentaResponseDTO> getVentaById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        VentaResponseDTO venta = ventaService.findByIdAndEmprendedorId(id, user.getEmprendedor().getId());
        return ResponseEntity.ok(venta);
    }

    /**
     * Obtiene el historial completo de ventas del emprendedor logueado.
     * @param user Usuario autenticado.
     * @return Lista de todas las ventas del emprendedor.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<List<VentaResponseDTO>> getAllVentas(@AuthenticationPrincipal User user) {
        List<VentaResponseDTO> ventas = ventaService.findAllByEmprendedorId(user.getEmprendedor().getId());
        return ResponseEntity.ok(ventas);
    }

    /**
     * Recupera todas las ventas asociadas a un cliente en particular.
     * @param clienteId ID del cliente a filtrar.
     * @param user Usuario autenticado (para validar que el cliente le pertenezca).
     * @return Lista de ventas realizadas al cliente especificado.
     */
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<List<VentaResponseDTO>> getAllVentasByCliente(
            @PathVariable Long clienteId,
            @AuthenticationPrincipal User user) {

        List<VentaResponseDTO> ventas = ventaService.findAllByClienteIdAndEmprendedorId(clienteId, user.getEmprendedor().getId());
        return ResponseEntity.ok(ventas);
    }

    /**
     * Elimina el registro de una venta asegurando la pertenencia del recurso.
     * @param id ID de la venta a eliminar.
     * @param user Usuario autenticado.
     * @return Respuesta vacía con estado 204 (NO_CONTENT).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<Void> deleteVenta(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        ventaService.delete(id, user.getEmprendedor().getId());
        return ResponseEntity.noContent().build();
    }
}