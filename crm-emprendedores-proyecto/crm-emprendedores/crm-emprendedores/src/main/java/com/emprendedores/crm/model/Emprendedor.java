package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "emprendedores")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
// Hereda: nombre, apellido, email, telefono, creadoEn, actualizadoEn
public class Emprendedor extends PersonaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rubro;
    private String pais;
    private String descripcion;
    private String fotoPerfil;

    // Relaciones de Propiedad (One-to-Many)
    // El emprendedor es el dueño de estas colecciones, mapeado por su FK en la otra tabla.

    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<Cliente> clientes;

    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<Contenido> contenidos;

    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<CategoriaContenido> categoriasContenido;

    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<User> users;

    // Las ventas son una relación indirecta (via Cliente o Ventas directas)
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<Venta> ventas;
}