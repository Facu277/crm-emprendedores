package com.emprendedores.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // <<-- ESTO ACTIVA @CreatedDate y @LastModifiedDate
public class JpaConfig {
}
