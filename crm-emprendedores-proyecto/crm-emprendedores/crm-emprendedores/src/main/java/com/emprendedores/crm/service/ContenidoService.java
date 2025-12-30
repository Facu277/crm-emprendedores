package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.contenido.ContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.contenido.ContenidoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContenidoService {
    ContenidoResponseDTO create(ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen);
    ContenidoResponseDTO update(Long id, ContenidoCreateUpdateDTO dto, Long emprendedorId, MultipartFile imagen);
    ContenidoResponseDTO findByIdAndEmprendedorId(Long id, Long emprendedorId);
    List<ContenidoResponseDTO> findAllByEmprendedorId(Long emprendedorId);
    void delete(Long id, Long emprendedorId);
}

