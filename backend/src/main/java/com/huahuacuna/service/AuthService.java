package com.huahuacuna.service;

import com.huahuacuna.model.LoginRequest;
import com.huahuacuna.model.LoginResponse;
import com.huahuacuna.model.User;

/**
 * Interfaz del servicio de autenticación.
 * Define los contratos para operaciones de autenticación.
 */
public interface AuthService {

    /**
     * Autentica un usuario con sus credenciales.
     *
     * @param loginRequest credenciales del usuario (email y password)
     * @return respuesta con token y datos del usuario
     * @throws RuntimeException si las credenciales son inválidas
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Obtiene un usuario por su email.
     *
     * @param email el email del usuario
     * @return el usuario encontrado
     * @throws RuntimeException si no existe
     */
    User getUserByEmail(String email);

    /**
     * Verifica si un email ya está registrado en el sistema.
     *
     * @param email el email a verificar
     * @return true si existe un usuario con ese email
     */
    boolean emailExists(String email);
}