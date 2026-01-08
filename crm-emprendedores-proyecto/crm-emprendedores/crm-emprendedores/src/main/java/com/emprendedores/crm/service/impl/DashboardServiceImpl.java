package com.emprendedores.crm.service.impl;

import com.emprendedores.crm.dto.cliente.ClienteMinResponseDTO;
import com.emprendedores.crm.dto.dashboard.DashboardResponseDTO;
import com.emprendedores.crm.dto.venta.VentaResponseDTO;
import com.emprendedores.crm.model.Venta;
import com.emprendedores.crm.repository.ClienteRepository;
import com.emprendedores.crm.repository.ContenidoRepository;
import com.emprendedores.crm.repository.VentaRepository;
import com.emprendedores.crm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Dashboard para la generación de métricas de negocio.
 * <p>
 * Esta clase orquestra múltiples repositorios (Clientes, Ventas y Contenidos) para
 * sintetizar la información relevante en un único objeto de respuesta, facilitando
 * la toma de decisiones basada en datos para el emprendedor.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final ContenidoRepository contenidoRepository;

    /**
     * Genera un resumen ejecutivo de las actividades y finanzas del emprendedor.
     * <p>
     * El cálculo de ingresos y volumen de ventas se realiza dinámicamente tomando como 
     * referencia el primer día del mes corriente a las 00:00:00.
     * </p>
     * @param emprendedorId Identificador único del emprendedor autenticado.
     * @return {@link DashboardResponseDTO} con métricas acumuladas y lista de actividad reciente.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getSummary(Long emprendedorId) {
        // Establecer el punto de inicio cronológico para las métricas mensuales
        LocalDateTime inicioMes = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        // 1. Recuperación de métricas cuantitativas
        long totalClientes = clienteRepository.countByEmprendedorId(emprendedorId);
        long ventasMes = ventaRepository.countVentasDelMes(emprendedorId, inicioMes);

        // Manejo de nulos en la sumatoria para evitar NullPointerException en el unboxing
        BigDecimal ingresosMes = ventaRepository.sumIngresosDelMes(emprendedorId, inicioMes);
        if (ingresosMes == null) ingresosMes = BigDecimal.ZERO;

        long contenidos = contenidoRepository.countContenidosPendientes(emprendedorId);

        // 2. Recuperación de actividad reciente (Últimas 5 ventas)
        List<VentaResponseDTO> ultimasVentas = ventaRepository.findTop5ByEmprendedorIdOrderByFechaDesc(emprendedorId)
                .stream()
                .map(this::mapToVentaResponseDTO)
                .collect(Collectors.toList());

        return DashboardResponseDTO.builder()
                .totalClientes(totalClientes)
                .ventasEsteMes(ventasMes)
                .ingresosEsteMes(ingresosMes)
                .contenidosProgramados(contenidos)
                .ultimasVentas(ultimasVentas)
                .build();
    }

    /**
     * Transforma una entidad {@link Venta} en un DTO de respuesta.
     * <p>
     * Incluye una proyección mínima del cliente asociada a la venta para optimizar
     * la visualización en la interfaz de usuario.
     * </p>
     * @param venta Entidad de venta origen.
     * @return DTO con la información formateada para el cliente (React/Frontend).
     */
    private VentaResponseDTO mapToVentaResponseDTO(Venta venta) {
        return VentaResponseDTO.builder()
                .id(venta.getId())
                .monto(venta.getMonto())
                .estado(venta.getEstado())
                .fecha(venta.getFecha())
                .metodoPago(venta.getMetodoPago())
                .cliente(ClienteMinResponseDTO.builder()
                        .id(venta.getCliente().getId())
                        .nombre(venta.getCliente().getNombre())
                        .apellido(venta.getCliente().getApellido())
                        .build())
                .build();
    }
}