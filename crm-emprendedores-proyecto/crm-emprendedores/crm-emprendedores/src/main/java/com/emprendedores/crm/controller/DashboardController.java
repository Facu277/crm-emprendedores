package com.emprendedores.crm.controller;

import com.emprendedores.crm.dto.dashboard.DashboardResponseDTO;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST dedicado a proveer métricas analíticas y resúmenes de actividad.
 * <p>
 * Actúa como un agregador de datos que consulta múltiples dominios (Ventas, Clientes, Contenidos)
 * para devolver una vista holística del estado del negocio del emprendedor. 
 * Este endpoint es el motor principal para la pantalla de inicio (Home/Dashboard) de la UI.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Recupera el resumen ejecutivo del emprendedor autenticado.
     * <p>
     * El método extrae automáticamente el identificador del emprendedor del contexto 
     * de seguridad de Spring para garantizar que las métricas (ventas del mes, 
     * clientes totales, contenidos pendientes) sean privadas y precisas.
     * </p>
     * @param user Objeto de usuario autenticado obtenido vía @AuthenticationPrincipal.
     * @return ResponseEntity con el {@link DashboardResponseDTO} que contiene los KPIs y actividad reciente.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<DashboardResponseDTO> getDashboard(@AuthenticationPrincipal User user) {
        // Aislamiento de datos: Solo se consultan métricas pertenecientes al emprendedor actual.
        Long emprendedorId = user.getEmprendedor().getId();
        return ResponseEntity.ok(dashboardService.getSummary(emprendedorId));
    }
}