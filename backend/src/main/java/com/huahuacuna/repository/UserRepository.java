package com.huahuacuna.repository;

import com.huahuacuna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email
     * @param email el email del usuario
     * @return Optional conteniendo el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si un usuario existe por email
     * @param email el email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}
