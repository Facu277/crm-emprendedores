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

@RestController
@RequestMapping("/api/v1/ventas") // Agregamos v1 por consistencia
@RequiredArgsConstructor // Reemplaza al constructor manual con Autowired
public class VentaController {

    private final VentaService ventaService;

    /**
     * Crea una nueva Venta.
     * El ID del emprendedor se obtiene del Token (@AuthenticationPrincipal).
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
     * Actualiza una Venta existente.
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
     * Obtiene una Venta por ID validando que sea del usuario autenticado.
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
     * Obtiene todas las Ventas del emprendedor logueado.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<List<VentaResponseDTO>> getAllVentas(@AuthenticationPrincipal User user) {
        List<VentaResponseDTO> ventas = ventaService.findAllByEmprendedorId(user.getEmprendedor().getId());
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene todas las Ventas de un cliente específico, validando propiedad.
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
     * Elimina una Venta asegurando que pertenece al usuario.
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