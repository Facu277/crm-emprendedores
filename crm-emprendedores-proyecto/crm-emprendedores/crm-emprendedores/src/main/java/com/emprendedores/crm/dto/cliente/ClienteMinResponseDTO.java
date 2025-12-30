package com.emprendedores.crm.dto.cliente;


import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class ClienteMinResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;

}