package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de persistencia de la entidad {@link Contenido}.
 * <p>
 * Incluye métodos para el acceso seguro a los contenidos, garantizando que cada 
 * emprendedor solo interactúe con su propio material y permitiendo métricas de gestión.
 * </p>
 * * @author Facundo Alfaro
 * @version 1.0
 */
@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    /**
     * Recupera todos los contenidos asociados a un emprendedor específico.
     * <p>Utilizado para mantener el aislamiento de datos (Multi-Tenancy) en el nivel de aplicación.</p>
     * * @param emprendedorId Identificador del emprendedor propietario.
     * @return Lista de contenidos pertenecientes al emprendedor.
     */
    List<Contenido> findByEmprendedorId(Long emprendedorId);

    /**
     * Cuenta la cantidad de contenidos cuya fecha de publicación es posterior a la hora actual.
     * * @param id Identificador del emprendedor.
     * @return Cantidad de contenidos programados para el futuro.
     */
    @Query("SELECT COUNT(c) FROM Contenido c WHERE c.emprendedor.id = :id AND c.fechaProgramada > CURRENT_TIMESTAMP")
    long countContenidosPendientes(Long id);

    /**
     * Cuenta cuántos contenidos están clasificados bajo una categoría específica.
     * * @param categoriaId ID de la categoría de contenido.
     * @return Total de contenidos asociados a dicha categoría.
     */
    long countByCategoriaContenidoId(Long categoriaId);

    /**
     * Filtra los contenidos de un emprendedor basándose en una categoría personalizada.
     * * @param emprendedorId Identificador del emprendedor propietario.
     * @param categoriaContenidoId Identificador de la categoría de contenido.
     * @return Lista de contenidos que coinciden con ambos criterios.
     */
    List<Contenido> findByEmprendedorIdAndCategoriaContenidoId(Long emprendedorId, Long categoriaContenidoId);
}

