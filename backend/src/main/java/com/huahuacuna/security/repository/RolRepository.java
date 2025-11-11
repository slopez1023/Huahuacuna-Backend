

package com.huahuacuna.security.repository;

import com.huahuacuna.security.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método para buscar un rol por su nombre
    Optional<Rol> findByNombre(String nombre);
}
