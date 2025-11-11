package com.huahuacuna.security.service;

import com.huahuacuna.security.model.Usuario;
import com.huahuacuna.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscamos al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // 2. Convertimos los roles de nuestra BD a los roles de Spring Security
        // (ESTA ES LA PARTE CORREGIDA)
        Set<GrantedAuthority> authorities = usuario
                .getRoles()
                .stream()
                .map((rol) -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toSet()); // Se recolecta como un Set

        // 3. Devolvemos el UserDetails que Spring Security entiende
        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}