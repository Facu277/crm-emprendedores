package com.emprendedores.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class MultipartJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        // Esta es la clave: permite leer la parte del DTO aunque
        // el navegador la envíe como application/json o incluso octet-stream
        return mediaType != null && (
                mediaType.isCompatibleWith(MediaType.APPLICATION_JSON) ||
                        mediaType.isCompatibleWith(MediaType.APPLICATION_OCTET_STREAM)
        );
    }

    @Override
    public boolean canWrite(MediaType mediaType) {
        return false; // Solo lo usamos para recibir datos
    }
}