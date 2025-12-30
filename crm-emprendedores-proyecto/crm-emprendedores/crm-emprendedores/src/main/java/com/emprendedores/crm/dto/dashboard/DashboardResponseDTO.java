package com.emprendedores.crm.dto.dashboard;

import com.emprendedores.crm.dto.venta.VentaMinResponseDTO;
import com.emprendedores.crm.dto.venta.VentaResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Builder
public class DashboardResponseDTO {
    private long totalClientes;
    private long ventasEsteMes;
    private BigDecimal ingresosEsteMes;
    private long contenidosProgramados;
    private List<VentaResponseDTO> ultimasVentas;
}
