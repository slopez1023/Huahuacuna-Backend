
package com.huahuacuna.apadrinamiento.dto;

import com.huahuacuna.apadrinamiento.model.EstadoNino;
import java.time.LocalDate;

public record CrearNinoRequest(
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String historia,
        String urlFotoPrincipal,
        EstadoNino estado
) {}
