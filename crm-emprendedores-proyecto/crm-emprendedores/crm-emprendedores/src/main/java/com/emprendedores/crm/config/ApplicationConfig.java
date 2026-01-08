package com.emprendedores.crm.config;

import com.emprendedores.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de infraestructura para la seguridad y autenticación de la aplicación.
 * <p>
 * Esta clase define los beans fundamentales que Spring Security utiliza para gestionar
 * el ciclo de vida de la autenticación, incluyendo la búsqueda de usuarios en la base 
 * de datos y el cifrado de contraseñas.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    /**
     * Define cómo recuperar los detalles del usuario desde la persistencia.
     * <p>
     * Se utiliza una expresión lambda para implementar la interfaz funcional, buscando
     * al usuario por su nombre de usuario (email) en el repositorio.
     * </p>
     * @return Implementación personalizada de {@link UserDetailsService}.
     * @throws UsernameNotFoundException si el usuario no existe en la base de datos.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Configura el proveedor de autenticación estándar de Spring Security.
     * <p>
     * Utiliza {@link DaoAuthenticationProvider}, el cual conecta el servicio de detalles 
     * de usuario con el codificador de contraseñas para validar las credenciales.
     * </p>
     * @return El proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expone el gestor de autenticación central de Spring Security.
     * <p>
     * Este componente es esencial para procesar las solicitudes de login en los
     * controladores de autenticación.
     * </p>
     * @param config Configuración de autenticación de Spring.
     * @return El {@link AuthenticationManager} del sistema.
     * @throws Exception si ocurre un error al obtener el gestor.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el algoritmo de hashing para las contraseñas.
     * <p>
     * Implementa {@link BCryptPasswordEncoder}, que es el estándar actual de la industria,
     * proporcionando un hashing robusto con "salt" automático.
     * </p>
     * @return Una instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}