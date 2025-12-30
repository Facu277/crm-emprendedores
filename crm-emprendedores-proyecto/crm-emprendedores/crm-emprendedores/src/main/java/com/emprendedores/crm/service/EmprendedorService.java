package com.emprendedores.crm.service;

import com.emprendedores.crm.dto.emprendedor.EmprendedorCreateUpdateDTO;
import com.emprendedores.crm.dto.emprendedor.EmprendedorResponseDTO;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface EmprendedorService {

    EmprendedorResponseDTO create(EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil);

    EmprendedorResponseDTO update(Long id, EmprendedorCreateUpdateDTO dto, MultipartFile fotoPerfil);

    EmprendedorResponseDTO findById(Long id);

    List<EmprendedorResponseDTO> findAll();

    void delete(Long id);

}
