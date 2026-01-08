package com.emprendedores.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Entidad que representa a un emprendedor dentro del sistema CRM.
 * <p>
 * Esta clase extiende de {@link PersonaBase}, por lo que hereda los atributos de identidad 
 * (nombre, apellido, email, teléfono) y los campos de auditoría de {@link BaseEntity}.
 * Es el núcleo del sistema, ya que actúa como propietario de clientes, contenidos, 
 * usuarios y registros de ventas.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Entity
@Table(name = "emprendedores")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Emprendedor extends PersonaBase {

    /**
     * Identificador único del emprendedor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Sector o área de negocio en la que se desempeña el emprendedor.
     */
    private String rubro;

    /**
     * País de residencia o de operación del negocio.
     */
    private String pais;

    /**
     * Breve reseña o información adicional sobre el emprendedor y su emprendimiento.
     */
    private String descripcion;

    /**
     * Ruta o URL de la imagen de perfil del emprendedor.
     */
    private String fotoPerfil;

    /**
     * Colección de clientes asociados a este emprendedor.
     * Relación uno a muchos con persistencia en cascada completa.
     */
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<Cliente> clientes;

    /**
     * Colección de contenidos creados por el emprendedor.
     * Relación uno a muchos con persistencia en cascada completa.
     */
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<Contenido> contenidos;

    /**
     * Categorías de contenido personalizadas definidas por el emprendedor.
     * Relación uno a muchos con persistencia en cascada completa.
     */
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<CategoriaContenido> categoriasContenido;

    /**
     * Usuarios vinculados a la cuenta del emprendedor para acceso al sistema.
     * Relación uno a muchos con persistencia en cascada completa.
     */
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<User> users;

    /**
     * Registro de todas las ventas realizadas por el emprendedor.
     * Relación uno a muchos con persistencia en cascada completa.
     */
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL)
    private Set<Venta> ventas;
}