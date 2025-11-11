
package com.huahuacuna.security.controller;

import com.huahuacuna.security.dto.AuthResponse;
import com.huahuacuna.security.dto.LoginRequest;
import com.huahuacuna.security.dto.RegistroRequest;
import com.huahuacuna.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Endpoint para iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    // Endpoint para registrar un usuario normal
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistroRequest registroRequest) {
        String response = authService.register(registroRequest);
        return ResponseEntity.ok(response);
    }

    // Endpoint (TEMPORAL) para registrar un admin
    // Úsalo una vez para crear tu admin y luego coméntalo o protégelo
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegistroRequest registroRequest) {
        String response = authService.registerAdmin(registroRequest);
        return ResponseEntity.ok(response);
    }
}