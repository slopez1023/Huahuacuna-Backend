package com.huahuacuna.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para seleccionar un niño para apadrinar.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectChildRequestDTO {

    @NotNull(message = "El ID del niño es obligatorio")
    private Long idNino;
}