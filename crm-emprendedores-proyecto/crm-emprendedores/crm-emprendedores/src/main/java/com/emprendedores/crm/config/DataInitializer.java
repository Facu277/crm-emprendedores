package com.emprendedores.crm.config;

import com.emprendedores.crm.model.Rol;
import com.emprendedores.crm.model.User;
import com.emprendedores.crm.repository.RolRepository;
import com.emprendedores.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Crear los Roles fundamentales
        Rol adminRol = crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("EMPRENDEDOR");
        crearRolSiNoExiste("EMPLEADO");

        // 2. Crear el Super Admin inicial si no existe ninguno
        // Esto te permite entrar al CRM la primera vez
        if (!userRepository.existsByUsername("admin@crm.com")) {
            User adminUser = User.builder()
                    .username("admin@crm.com")
                    .password(passwordEncoder.encode("admin1234")) // ¡Cambia esto después!
                    .rol(adminRol)
                    .build();

            userRepository.save(adminUser);
            System.out.println(">>> LOG CRM: Super Admin creado (admin@crm.com / admin1234)");
        }
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol nuevoRol = Rol.builder().nombre(nombre).build();
                    System.out.println(">>> LOG CRM: Rol creado -> " + nombre);
                    return rolRepository.save(nuevoRol);
                });
    }
}
