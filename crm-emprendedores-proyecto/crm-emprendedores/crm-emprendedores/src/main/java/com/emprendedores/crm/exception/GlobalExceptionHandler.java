package com.emprendedores.crm.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de asesoramiento (Advice) para la gestión centralizada de excepciones.
 * <p>
 * Esta clase intercepta las excepciones lanzadas por los controladores y servicios,
 * transformándolas en respuestas JSON estandarizadas. Mejora la experiencia del cliente
 * API al proporcionar detalles claros sobre errores de validación, seguridad y lógica de negocio.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de Bean Validation (@Valid).
     * <p>
     * Extrae cada campo que falló y su mensaje asociado (ej. "el email no puede estar vacío")
     * y los agrupa en un mapa de errores para que el frontend pueda señalarlos visualmente.
     * </p>
     * @param ex Excepción de validación de argumentos.
     * @return Respuesta con estado 400 (BAD_REQUEST) y el desglose de errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Error de validación en los campos");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando no se encuentra una entidad en la base de datos.
     * @param ex Excepción de entidad no encontrada.
     * @return Respuesta con estado 404 (NOT_FOUND).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Maneja fallos en la autenticación del usuario.
     * <p>
     * Por seguridad, se devuelve un mensaje genérico para no revelar si lo que falló
     * fue específicamente el usuario o la contraseña.
     * </p>
     * @param ex Excepción de credenciales inválidas.
     * @return Respuesta con estado 401 (UNAUTHORIZED).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    /**
     * Maneja las excepciones controladas lanzadas explícitamente en la capa de servicio.
     * @param ex Excepción con estado y razón específica.
     * @return Respuesta con el código de estado definido en la excepción original.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        return buildResponse((HttpStatus) ex.getStatusCode(), ex.getReason());
    }

    /**
     * Maneja errores de argumentos ilegales o inesperados.
     * @param ex Excepción de argumento ilegal.
     * @return Respuesta con estado 400 (BAD_REQUEST).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Capturador de último recurso para cualquier excepción no controlada.
     * <p>
     * Registra la traza del error en el log del servidor y devuelve un error 500.
     * Es vital para evitar que el servidor exponga trazas de error crudas al cliente.
     * </p>
     * @param ex Cualquier excepción no capturada previamente.
     * @return Respuesta con estado 500 (INTERNAL_SERVER_ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        ex.printStackTrace(); // Recomendado usar un Logger en producción
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getLocalizedMessage());
    }

    /**
     * Método de utilidad para construir una estructura de respuesta consistente.
     * @param status Estado HTTP a retornar.
     * @param message Mensaje descriptivo del error.
     * @return Objeto ResponseEntity con el mapa de respuesta.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}