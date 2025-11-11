
package com.huahuacuna.security.service;

import com.huahuacuna.security.dto.AuthResponse;
import com.huahuacuna.security.dto.LoginRequest;
import com.huahuacuna.security.dto.RegistroRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    String register(RegistroRequest registroRequest);
    String registerAdmin(RegistroRequest registroRequest); // Método especial
}
