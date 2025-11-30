package com.huahuacuna.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una nueva entrada de bitácora.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateLogEntryDTO {
    private String titulo;
    private String contenido;
    private String tipoEntrada;
}
