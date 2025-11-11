package com.huahuacuna;

import com.huahuacuna.security.model.Rol;
import com.huahuacuna.security.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HuahuacunaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuahuacunaApplication.class, args);
    }

    // Este Bean se ejecutará al iniciar la aplicación
    // y creará los roles si no existen.
    @Bean
    CommandLineRunner run(RolRepository rolRepository) {
        return args -> {
            if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
                rolRepository.save(new Rol("ROLE_ADMIN"));
            }
            if (rolRepository.findByNombre("ROLE_USER").isEmpty()) {
                rolRepository.save(new Rol("ROLE_USER"));
            }
        };
    }
}