package com.huahuacuna.repository;

import com.huahuacuna.model.Role;
import com.huahuacuna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD sobre la entidad User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email.
     *
     * @param email el correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email el correo electrónico a verificar
     * @return true si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por su token de recuperación de contraseña.
     *
     * @param token el token de recuperación
     * @return Optional con el usuario si el token existe
     */
    Optional<User> findByResetPasswordToken(String token);

    /**
     * Busca todos los usuarios con un rol específico.
     *
     * @param role el rol a buscar
     * @return lista de usuarios con ese rol
     */
    List<User> findByRole(Role role);

    /**
     * Busca usuarios activos con un rol específico.
     *
     * @param role     el rol a buscar
     * @param isActive estado de activación
     * @return lista de usuarios activos con ese rol
     */
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);
}