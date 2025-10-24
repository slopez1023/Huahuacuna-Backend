package com.huahuacuna.service;

import com.huahuacuna.model.LoginRequest;
import com.huahuacuna.model.LoginResponse;
import com.huahuacuna.model.RegisterRequest;
import com.huahuacuna.model.RegisterResponse;

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
    RegisterResponse register(RegisterRequest request);

}