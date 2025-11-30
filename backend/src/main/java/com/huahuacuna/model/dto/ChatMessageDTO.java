package com.huahuacuna.model.dto;

import com.huahuacuna.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mensajes del chat.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private Long id;
    private Long apadrinamientoId;
    private String contenido;
    private String enviadoPor;
    private String fecha;
    private Boolean leido;
    private String fechaLectura;

    /**
     * Convierte una entidad ChatMessage a DTO.
     */
    public static ChatMessageDTO fromEntity(ChatMessage message) {
        if (message == null) return null;

        return ChatMessageDTO.builder()
                .id(message.getId())
                .apadrinamientoId(message.getSponsorship() != null ?
                        message.getSponsorship().getId() : null)
                .contenido(message.getContent())
                .enviadoPor(mapSentBy(message.getSentBy()))
                .fecha(message.getCreatedAt() != null ?
                        message.getCreatedAt().toString() : null)
                .leido(message.getIsRead())
                .fechaLectura(message.getReadAt() != null ?
                        message.getReadAt().toString() : null)
                .build();
    }

    /**
     * Mapea quién envió el mensaje.
     */
    private static String mapSentBy(ChatMessage.SentBy sentBy) {
        if (sentBy == null) return "PADRINO";
        return switch (sentBy) {
            case GODPARENT -> "PADRINO";
            case ADMIN -> "ADMINISTRADOR";
        };
    }
}

