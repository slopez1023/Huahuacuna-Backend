package com.huahuacuna.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de inhabilitación de un niño.
 * Contiene la razón por la cual se inhabilita.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InactivateRequestDTO {

    private String reason;

}