
package com.huahuacuna.security.jwt;

import com.huahuacuna.security.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-ms}")
    private int jwtExpirationInMs;

    private final SecretKey key;

    // Generamos una clave segura para HS512
    public JwtTokenProvider(@Value("${app.jwt-secret}") String secret) {
        // Asegúrate de que el secreto tenga la longitud suficiente para HS512
        // Si es muy corto, usa Keys.hmacShaKeyFor(secret.getBytes())
        // Para una clave robusta generada una vez:
        // SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        // String secretString = Encoders.BASE64.encode(key.getEncoded());
        // Por simplicidad, si tu 'app.jwt-secret' es largo y seguro:
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generar un token JWT a partir de la autenticación
    public String generarToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Obtener el email (username) del token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Validar el token
    public boolean validarToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            logger.error("Claims de JWT vacías");
        }
        return false;
    }
}