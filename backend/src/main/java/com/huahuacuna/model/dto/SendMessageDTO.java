package com.huahuacuna.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar un nuevo mensaje.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class SendMessageDTO {
    private String contenido;
}
