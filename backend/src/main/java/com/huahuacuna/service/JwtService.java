package com.huahuacuna.service;

import com.huahuacuna.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para generación y validación de tokens JWT.
 * Utiliza JJWT para crear tokens firmados con HS256.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Genera un token JWT para un usuario autenticado.
     *
     * @param user el usuario autenticado
     * @return token JWT firmado
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("fullName", user.getFullName());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        log.info("Token JWT generado para usuario: {}", user.getEmail());
        return token;
    }

    /**
     * Extrae el email (subject) del token JWT.
     *
     * @param token el token JWT
     * @return el email del usuario
     */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrae el ID del usuario del token JWT.
     *
     * @param token el token JWT
     * @return el ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /**
     * Extrae el rol del usuario del token JWT.
     *
     * @param token el token JWT
     * @return el rol del usuario como String
     */
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Valida si un token JWT es válido.
     *
     * @param token el token JWT a validar
     * @return true si el token es válido y no ha expirado
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Error validando token JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrae todos los claims del token JWT.
     *
     * @param token el token JWT
     * @return los claims del token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave de firma para JWT.
     *
     * @return la SecretKey para firmar tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}