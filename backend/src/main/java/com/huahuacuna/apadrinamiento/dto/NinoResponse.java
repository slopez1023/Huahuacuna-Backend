

package com.huahuacuna.apadrinamiento.dto;

import com.huahuacuna.apadrinamiento.model.EstadoNino;
import java.time.LocalDate;

public record NinoResponse(
        Long id,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String historia,
        String urlFotoPrincipal,
        EstadoNino estado
) {
    // Los 'record' son la forma completa y moderna de DTOs.
    // No se necesita nada más.
}
