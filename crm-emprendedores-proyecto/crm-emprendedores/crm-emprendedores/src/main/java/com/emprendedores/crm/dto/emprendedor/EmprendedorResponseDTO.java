package com.emprendedores.crm.dto.emprendedor;

import com.emprendedores.crm.dto.cliente.ClienteMinResponseDTO;
import com.emprendedores.crm.dto.categoriacontenido.CategoriaContenidoMinResponseDTO;
import com.emprendedores.crm.dto.contenido.ContenidoMinResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class EmprendedorResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rubro;
    private String pais;
    private String descripcion;
    private String fotoPerfil;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // --- Relaciones (Opcionales, generalmente se devuelven en endpoints dedicados) ---
    // Si decides incluirlos, usarías los DTOs mínimos de las entidades relacionadas:

    /*
    private List<ClienteMinResponseDTO> clientes;
    private List<ContenidoMinResponseDTO> contenidos;
    private List<CategoriaContenidoMinResponseDTO> categoriasContenido;
    // ... otros
    */
}