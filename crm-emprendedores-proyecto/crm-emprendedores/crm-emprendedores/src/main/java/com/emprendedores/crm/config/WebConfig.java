package com.emprendedores.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Mantienes tu configuración de convertidores (la que arregló el error de JSON)
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

    // AGREGAS ESTO para poder ver las imágenes desde el navegador
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Usamos System.getProperty("user.dir") para que funcione en cualquier PC
        // donde descargues el proyecto, siempre que la carpeta 'uploads' esté en la raíz.
        String rootPath = System.getProperty("user.dir");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + rootPath + "/uploads/");
    }
}
