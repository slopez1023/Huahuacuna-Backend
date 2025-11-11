

package com.huahuacuna.apadrinamiento.dto;

import java.time.LocalDate;

// No incluimos el estado, ya que se manejará con un endpoint separado.
public record ActualizarNinoRequest(
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String historia,
        String urlFotoPrincipal
) {}
