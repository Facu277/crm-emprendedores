package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.cliente.ClienteCreateUpdateDTO;
import com.emprendedores.crm.dto.cliente.ClienteResponseDTO;
import java.util.List;

public interface ClienteService {

    // Ahora recibe el ID del emprendedor autenticado
    ClienteResponseDTO create(ClienteCreateUpdateDTO dto, Long emprendedorId);

    ClienteResponseDTO update(Long id, ClienteCreateUpdateDTO dto, Long emprendedorId);

    ClienteResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId);

    List<ClienteResponseDTO> findAllByEmprendedorId(Long emprendedorId);

    void delete(Long id, Long emprendedorId);
}