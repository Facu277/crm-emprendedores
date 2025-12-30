package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.dashboard.DashboardResponseDTO;

public interface DashboardService {
    /**
     * Recupera las métricas clave y actividad reciente para el panel principal.
     * @param emprendedorId ID del emprendedor autenticado.
     * @return DTO con estadísticas de clientes, ventas e ingresos.
     */
    DashboardResponseDTO getSummary(Long emprendedorId);
}
