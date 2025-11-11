

package com.huahuacuna.apadrinamiento.dto;

import com.huahuacuna.apadrinamiento.model.EstadoNino;

public record CambiarEstadoRequest(
        EstadoNino nuevoEstado
) {}
