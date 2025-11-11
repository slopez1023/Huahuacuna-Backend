
package com.huahuacuna.security.service;

import com.huahuacuna.apadrinamiento.exception.ResourceNotFoundException;
import com.huahuacuna.security.dto.AuthResponse;
import com.huahuacuna.security.dto.LoginRequest;
import com.huahuacuna.security.dto.RegistroRequest;
import com.huahuacuna.security.jwt.JwtTokenProvider;
import com.huahuacuna.security.model.Rol;
import com.huahuacuna.security.model.Usuario;
import com.huahuacuna.security.repository.RolRepository;
import com.huahuacuna.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        // Autenticar al usuario con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // Establecer la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar el token JWT
        String token = tokenProvider.generarToken(authentication);

        return new AuthResponse(token);
    }

    @Override
    public String register(RegistroRequest registroRequest) {
        return registerUser(registroRequest, "ROLE_USER");
    }

    @Override
    public String registerAdmin(RegistroRequest registroRequest) {
        // En un mundo real, este endpoint estaría protegido
        return registerUser(registroRequest, "ROLE_ADMIN");
    }

    private String registerUser(RegistroRequest registroRequest, String roleName) {
        // Validar si el email ya existe
        if (usuarioRepository.findByEmail(registroRequest.email()).isPresent()) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }

        // Crear el nuevo usuario
        Usuario usuario = new Usuario(
                registroRequest.nombre(),
                registroRequest.email(),
                passwordEncoder.encode(registroRequest.password())
        );

        // Asignar roles
        Set<Rol> roles = new HashSet<>();
        Rol userRol = rolRepository.findByNombre(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName));
        roles.add(userRol);

        usuario.setRoles(roles);

        // Guardar usuario
        usuarioRepository.save(usuario);

        return "Usuario registrado exitosamente!";
    }
}