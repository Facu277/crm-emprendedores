package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.dashboard.DashboardResponseDTO;

/**
 * Interfaz de servicio para la generación de analíticas y resúmenes de actividad.
 * <p>
 * Se encarga de consolidar datos de múltiples entidades (Ventas, Clientes, Contenidos)
 * para presentar una vista unificada del estado del negocio.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
public interface DashboardService {
    /**
     * Recupera las métricas clave y actividad reciente para el panel principal.
     * <p>
     * Calcula estadísticas como ingresos totales del mes, crecimiento de clientes 
     * y las últimas transacciones registradas.
     * </p>
     * @param emprendedorId ID del emprendedor autenticado.
     * @return {@link DashboardResponseDTO} con el resumen ejecutivo del negocio.
     */
    DashboardResponseDTO getSummary(Long emprendedorId);
}
