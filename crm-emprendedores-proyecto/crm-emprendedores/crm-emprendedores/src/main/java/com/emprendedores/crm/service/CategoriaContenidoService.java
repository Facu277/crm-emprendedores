package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoCreateUpdateDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoResponseDTO;
import com.emprendedores.crm.model.CategoriaContenido;

import java.util.List;

public interface CategoriaContenidoService {
    CategoriaContenidoResponseDTO create(CategoriaContenidoCreateUpdateDTO dto, String username);
    CategoriaContenidoResponseDTO update(Long id, CategoriaContenidoCreateUpdateDTO dto, String username);
    CategoriaContenidoResponseDTO findByIdAndUsername(Long id, String username);
    List<CategoriaContenidoResponseDTO> findAllByUsername(String username);
    void delete(Long id, String username);
}