package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link Categoria}.
 * <p>
 * Maneja las categorías globales del sistema que están disponibles para todos los usuarios.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Verifica la existencia de una categoría basada en su nombre.
     * * @param nombre Nombre de la categoría a validar.
     * @return {@code true} si ya existe una categoría con ese nombre, {@code false} en caso contrario.
     */
    boolean existsByNombre(String nombre);
}

