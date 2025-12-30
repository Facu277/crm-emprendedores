package com.emprendedores.crm.repository;

import com.emprendedores.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método estándar para Spring Security
    Optional<User> findByUsername(String username);

    // NUEVO MÉTODO: Trae al usuario con su emprendedor y rol cargados
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.emprendedor " +
            "LEFT JOIN FETCH u.rol " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithDetails(@Param("username") String username);

    boolean existsByUsername(String username);

    List<User> findByEmprendedorId(Long emprendedorId);
}