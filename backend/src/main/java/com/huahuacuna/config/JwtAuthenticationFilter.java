package com.huahuacuna.config;

import com.huahuacuna.model.Role;
import com.huahuacuna.model.User;
import com.huahuacuna.repository.UserRepository;
import com.huahuacuna.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro JWT que intercepta cada petición HTTP para validar el token.
 * Se ejecuta una sola vez por request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extraer token del header Authorization
            String token = extractTokenFromRequest(request);

            if (token != null && jwtService.validateToken(token)) {
                // Extraer email del token
                String email = jwtService.getEmailFromToken(token);

                // Buscar usuario en la base de datos
                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null && user.getIsActive()) {
                    // Crear autenticación con rol
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority(user.getRole().getAuthority())
                                    )
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Usuario autenticado vía JWT: {} con rol {}", email, user.getRole());
                }
            }
        } catch (Exception e) {
            log.error("Error procesando JWT: {}", e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization.
     *
     * @param request la petición HTTP
     * @return el token sin el prefijo "Bearer ", o null si no existe
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remover "Bearer "
        }

        return null;
    }
}