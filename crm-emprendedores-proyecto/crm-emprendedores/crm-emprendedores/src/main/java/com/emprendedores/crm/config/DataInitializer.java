package com.emprendedores.crm.config;

import com.emprendedores.crm.model.Rol;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.repository.RolRepository;
import com.emprendedores.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente de inicialización de datos maestros del CRM.
 * <p>
 * Se ejecuta automáticamente al iniciar la aplicación para garantizar que la base de datos
 * posea la infraestructura mínima necesaria (Roles y Usuario Administrador). 
 * Utiliza una lógica de verificación previa para evitar la duplicación de registros
 * en reinicios del servidor.
 * </p>
 * @author Facundo Alfaro
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Punto de entrada de la inicialización de Spring Boot.
     * <p>
     * Orquestra la creación de roles fundamentales y del primer super-administrador
     * del sistema. Imprime logs en consola para notificar el estado del aprovisionamiento.
     * </p>
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    @Override
    public void run(String... args) {
        // 1. Crear la jerarquía de permisos fundamental
        Rol adminRol = crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("EMPRENDEDOR");
        crearRolSiNoExiste("EMPLEADO");

        // 2. Aprovisionar credenciales maestras iniciales
        // Este bloque permite el acceso inicial para configurar el resto del sistema.
        if (!userRepository.existsByUsername("admin@crm.com")) {
            User adminUser = User.builder()
                    .username("admin@crm.com")
                    .password(passwordEncoder.encode("admin1234")) 
                    .rol(adminRol)
                    .build();

            userRepository.save(adminUser);
            System.out.println(">>> LOG CRM: Super Admin creado (admin@crm.com / admin1234)");
        }
    }

    /**
     * Busca un rol en la base de datos y lo crea si no está presente.
     * @param nombre El nombre identificador del rol (ej. 'ADMIN').
     * @return El objeto Rol persistido o recuperado.
     */
    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol nuevoRol = Rol.builder().nombre(nombre).build();
                    System.out.println(">>> LOG CRM: Rol creado -> " + nombre);
                    return rolRepository.save(nuevoRol);
                });
    }
}
