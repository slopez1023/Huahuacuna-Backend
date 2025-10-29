package com.huahuacuna.service;

import com.huahuacuna.exception.AuthenticationException;
import com.huahuacuna.model.LoginRequest;
import com.huahuacuna.model.LoginResponse;

/**
 * Interfaz de servicio de autenticación.
 * Define el contrato para operaciones de login.
 * Facilita la inyección de dependencias y testing.
 */
public interface AuthService {

    /**
     * Autentica un usuario con email y contraseña.
     *
     * @param loginRequest contiene email y password
     * @return LoginResponse con información de autenticación
     * @throws AuthenticationException si las credenciales son inválidas
     */
    LoginResponse authenticate(LoginRequest loginRequest);

}