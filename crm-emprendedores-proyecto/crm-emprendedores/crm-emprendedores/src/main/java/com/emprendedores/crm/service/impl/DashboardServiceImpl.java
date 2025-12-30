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

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final ContenidoRepository contenidoRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getSummary(Long emprendedorId) {
        LocalDateTime inicioMes = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        // 1. Métricas numéricas
        long totalClientes = clienteRepository.countByEmprendedorId(emprendedorId);
        long ventasMes = ventaRepository.countVentasDelMes(emprendedorId, inicioMes);

        BigDecimal ingresosMes = ventaRepository.sumIngresosDelMes(emprendedorId, inicioMes);
        if (ingresosMes == null) ingresosMes = BigDecimal.ZERO;

        long contenidos = contenidoRepository.countContenidosPendientes(emprendedorId);

        // 2. Obtener las últimas 5 ventas (¡Lo que faltaba!)
        List<VentaResponseDTO> ultimasVentas = ventaRepository.findTop5ByEmprendedorIdOrderByFechaDesc(emprendedorId)
                .stream()
                .map(this::mapToVentaResponseDTO)
                .collect(Collectors.toList());

        return DashboardResponseDTO.builder()
                .totalClientes(totalClientes)
                .ventasEsteMes(ventasMes)
                .ingresosEsteMes(ingresosMes)
                .contenidosProgramados(contenidos)
                .ultimasVentas(ultimasVentas) // Asignamos la lista al DTO
                .build();
    }

    // Método helper para mapear de Entidad a DTO
    private VentaResponseDTO mapToVentaResponseDTO(Venta venta) {
        return VentaResponseDTO.builder()
                .id(venta.getId())
                .monto(venta.getMonto())
                .estado(venta.getEstado())
                .fecha(venta.getFecha())
                .metodoPago(venta.getMetodoPago())
                // Mapeamos el cliente anidado para que React lo vea
                .cliente(ClienteMinResponseDTO.builder()
                        .id(venta.getCliente().getId())
                        .nombre(venta.getCliente().getNombre())
                        .apellido(venta.getCliente().getApellido())
                        .build())
                .build();
    }
}
