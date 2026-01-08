package com.emprendedores.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuración para habilitar la auditoría automática de JPA.
 * <p>
 * Permite el uso de anotaciones como {@code @CreatedDate} y {@code @LastModifiedDate} 
 * en las entidades para el seguimiento automático de marcas temporales.
 * </p>
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
