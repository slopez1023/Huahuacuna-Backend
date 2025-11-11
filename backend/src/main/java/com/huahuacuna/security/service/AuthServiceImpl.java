
package com.huahuacuna.security.service;

import com.huahuacuna.apadrinamiento.exception.ResourceNotFoundException;
import com.huahuacuna.security.dto.AuthResponse;
import com.huahuacuna.security.dto.LoginRequest;
import com.huahuacuna.security.dto.RegistroRequest;
import com.huahuacuna.security.dto.UsuarioDto; // <-- IMPORTAR
import com.huahuacuna.security.jwt.JwtTokenProvider;
import com.huahuacuna.security.model.Rol;
import com.huahuacuna.security.model.Usuario; // <-- IMPORTAR
import com.huahuacuna.security.repository.RolRepository;
import com.huahuacuna.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // <-- IMPORTAR
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors; // <-- IMPORTAR

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

        // 1. Autenticar (esto sigue igual)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // 2. Establecer en contexto (esto sigue igual)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generar el token (esto sigue igual)
        String token = tokenProvider.generarToken(authentication);

        // 4. --- (NUEVO) OBTENER LA INFO DEL USUARIO ---
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 5. --- (NUEVO) CONVERTIR ROLES A STRINGS ---
        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        // 6. --- (NUEVO) CREAR EL USUARIO DTO ---
        UsuarioDto usuarioDto = new UsuarioDto(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                roles
        );

        // 7. --- (MODIFICADO) DEVOLVER LA RESPUESTA COMPLETA ---
        return new AuthResponse(token, usuarioDto);
    }

    // ... (El resto de tus métodos 'register' y 'registerAdmin' siguen igual) ...

    @Override
    public String register(RegistroRequest registroRequest) {
        return registerUser(registroRequest, "ROLE_USER");
    }

    @Override
    public String registerAdmin(RegistroRequest registroRequest) {
        return registerUser(registroRequest, "ROLE_ADMIN");
    }

    private String registerUser(RegistroRequest registroRequest, String roleName) {
        if (usuarioRepository.findByEmail(registroRequest.email()).isPresent()) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }
        Usuario usuario = new Usuario(
                registroRequest.nombre(),
                registroRequest.email(),
                passwordEncoder.encode(registroRequest.password())
        );
        Set<Rol> roles = new HashSet<>();
        Rol userRol = rolRepository.findByNombre(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName));
        roles.add(userRol);
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente!";
    }
}