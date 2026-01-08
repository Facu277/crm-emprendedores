package com.emprendedores.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * Convertidor personalizado para manejar DTOs dentro de peticiones Multipart.
 * <p>
 * Permite que Jackson procese partes de una petición multipart incluso si el cliente 
 * (navegador o Postman) no especifica correctamente el Content-Type como 'application/json',
 * aceptando también 'application/octet-stream'.
 * </p>
 */
@Component
public class MultipartJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    /**
     * Define qué tipos de contenido puede leer este convertidor.
     * <p>Se expande la compatibilidad para incluir flujos de datos binarios que contienen JSON.</p>
     */
    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return mediaType != null && (
                mediaType.isCompatibleWith(MediaType.APPLICATION_JSON) ||
                mediaType.isCompatibleWith(MediaType.APPLICATION_OCTET_STREAM)
        );
    }

    @Override
    public boolean canWrite(MediaType mediaType) {
        return false; // El convertidor solo se activa para la entrada (lectura de peticiones)
    }
}