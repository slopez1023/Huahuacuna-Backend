package com.huahuacuna.service;

import com.huahuacuna.model.User;
import com.huahuacuna.model.dto.CreateUserDTO;
import com.huahuacuna.model.dto.UpdateUserDTO;
import java.util.List;

/**
 * Interfaz de servicio para gestión de usuarios
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public interface UserService {

    /**
     * Crea un nuevo usuario (solo ADMIN)
     */
    User createUser(CreateUserDTO dto);

    /**
     * Obtiene todos los usuarios
     */
    List<User> getAllUsers();

    /**
     * Obtiene un usuario por ID
     */
    User getUserById(Long id);

    /**
     * Actualiza información de un usuario
     */
    User updateUser(Long id, UpdateUserDTO dto);

    /**
     * Elimina un usuario (soft delete - marca como inactivo)
     */
    void deleteUser(Long id);

    /**
     * Activa o desactiva un usuario
     */
    User toggleUserStatus(Long id);

    /**
     * Resetea la contraseña de un usuario
     */
    void resetUserPassword(Long id, String newPassword);

    /**
     * Obtiene usuarios por rol
     */
    List<User> getUsersByRole(String role);

    /**
     * Busca usuarios por nombre o email
     */
    List<User> searchUsers(String searchTerm);
}