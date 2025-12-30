package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.venta.VentaCreateUpdateDTO;
import com.emprendedores.crm.dto.venta.VentaResponseDTO;

import java.util.List;

public interface VentaService {
    VentaResponseDTO create(VentaCreateUpdateDTO dto, Long emprendedorId);
    VentaResponseDTO update(Long id, VentaCreateUpdateDTO dto, Long emprendedorId);
    VentaResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId);
    List<VentaResponseDTO> findAllByEmprendedorId(Long emprendedorId);
    List<VentaResponseDTO> findAllByClienteIdAndEmprendedorId(Long clienteId, Long emprendedorId);
    void delete(Long id, Long emprendedorId);
}

