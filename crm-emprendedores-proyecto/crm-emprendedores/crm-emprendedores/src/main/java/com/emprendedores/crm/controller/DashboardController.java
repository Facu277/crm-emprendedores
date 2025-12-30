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

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRENDEDOR')")
    public ResponseEntity<DashboardResponseDTO> getDashboard(@AuthenticationPrincipal User user) {
        // Extraemos el emprendedor del usuario autenticado
        Long emprendedorId = user.getEmprendedor().getId();
        return ResponseEntity.ok(dashboardService.getSummary(emprendedorId));
    }
}
