

package com.huahuacuna.apadrinamiento.repository;

import com.huahuacuna.apadrinamiento.model.Nino;
import com.huahuacuna.apadrinamiento.model.EstadoNino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NinoRepository extends JpaRepository<Nino, Long> {

    // Spring Data JPA crea la consulta por ti basado en el nombre del método
    List<Nino> findByEstado(EstadoNino estado);
}
