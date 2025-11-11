

package com.huahuacuna.apadrinamiento.mapper;

import com.huahuacuna.apadrinamiento.dto.CrearNinoRequest;
import com.huahuacuna.apadrinamiento.dto.NinoResponse;
import com.huahuacuna.apadrinamiento.model.Nino;
import org.springframework.stereotype.Component;

@Component // Le dice a Spring que gestione esta clase
public class NinoMapper {

    /**
     * Convierte una Entidad (de la BD) a un DTO de Respuesta (para el frontend).
     */
    public NinoResponse toNinoResponse(Nino nino) {
        return new NinoResponse(
                nino.getId(),
                nino.getNombres(),
                nino.getApellidos(),
                nino.getFechaNacimiento(),
                nino.getHistoria(),
                nino.getUrlFotoPrincipal(),
                nino.getEstado()
        );
    }

    /**
     * Convierte un DTO de Creación (del frontend) a una Entidad (para la BD).
     */
    public Nino toNino(CrearNinoRequest request) {
        return new Nino(
                request.nombres(),
                request.apellidos(),
                request.fechaNacimiento(),
                request.historia(),
                request.urlFotoPrincipal(),
                request.estado() // El estado inicial
        );
    }
}
