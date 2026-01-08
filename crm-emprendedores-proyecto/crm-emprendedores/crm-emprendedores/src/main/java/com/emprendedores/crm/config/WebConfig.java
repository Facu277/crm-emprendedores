package com.emprendedores.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuración de infraestructura Web MVC.
 * <p>
 * Gestiona la exposición de archivos estáticos (imágenes subidas) y extiende
 * los convertidores de mensajes para soportar flujos de datos binarios en JSON.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Mapea URLs de tipo /uploads/** a la carpeta física 'uploads' en la raíz del proyecto.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rootPath = System.getProperty("user.dir");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + rootPath + "/uploads/");
    }

    /**
     * Amplía la compatibilidad de Jackson para procesar JSON dentro de peticiones
     * multipart que lleguen como application/octet-stream.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .map(c -> (MappingJackson2HttpMessageConverter) c)
                .forEach(c -> {
                    List<org.springframework.http.MediaType> types = new java.util.ArrayList<>(c.getSupportedMediaTypes());
                    types.add(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
                    c.setSupportedMediaTypes(types);
                });
    }
}
