package com.emprendedores.crm.dto.cliente;

import com.emprendedores.crm.dto.emprendedor.EmprendedorMinResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter @Setter
@Builder // O SuperBuilder si heredas de algún lado
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String etiqueta;

    // Relación Many-to-One: Esto mostrará quién es el dueño del cliente
    private EmprendedorMinResponseDTO emprendedor;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}