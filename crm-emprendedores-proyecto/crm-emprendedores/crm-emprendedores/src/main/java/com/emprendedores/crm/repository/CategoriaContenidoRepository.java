package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.CategoriaContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio encargado de la persistencia y acceso a datos de la entidad {@link CategoriaContenido}.
 * <p>
 * Esta interfaz extiende de {@link JpaRepository}, lo que permite realizar operaciones CRUD 
 * estándar y consultas personalizadas sobre la tabla de categorías de contenido.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface CategoriaContenidoRepository extends JpaRepository<CategoriaContenido, Long> {

    /**
     * Recupera una lista de categorías de contenido asociadas a un emprendedor específico.
     * * @param emprendedorId Identificador único del emprendedor.
     * @return {@link List} de {@link CategoriaContenido} que pertenecen al emprendedor solicitado.
     */
    List<CategoriaContenido> findByEmprendedorId(Long emprendedorId);

    /**
     * Verifica si ya existe una categoría con un nombre específico para un emprendedor determinado.
     * <p>
     * Se utiliza principalmente para validar la restricción de unicidad antes de intentar 
     * crear o actualizar un registro.
     * </p>
     * * @param nombre El nombre de la categoría a buscar.
     * @param emprendedorId Identificador del emprendedor propietario.
     * @return {@code true} si la categoría ya existe, {@code false} en caso contrario.
     */
    boolean existsByNombreAndEmprendedorId(String nombre, Long emprendedorId);
}