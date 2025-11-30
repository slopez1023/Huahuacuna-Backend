package com.huahuacuna.model.dto;

import com.huahuacuna.model.LogEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para entradas de bitácora.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntryDTO {

    private Long id;
    private Long apadrinamientoId;
    private String titulo;
    private String contenido;
    private String tipoEntrada;
    private String registradoPor;
    private String fecha;
    private String fechaActualizacion;

    /**
     * Convierte una entidad LogEntry a DTO.
     */
    public static LogEntryDTO fromEntity(LogEntry entry) {
        if (entry == null) return null;

        return LogEntryDTO.builder()
                .id(entry.getId())
                .apadrinamientoId(entry.getSponsorship() != null ?
                        entry.getSponsorship().getId() : null)
                .titulo(entry.getTitle())
                .contenido(entry.getContent())
                .tipoEntrada(entry.getEntryType() != null ?
                        entry.getEntryType().getDisplayName() : "General")
                .registradoPor(mapRegisteredBy(entry.getRegisteredBy()))
                .fecha(entry.getCreatedAt() != null ?
                        entry.getCreatedAt().toString() : null)
                .fechaActualizacion(entry.getUpdatedAt() != null ?
                        entry.getUpdatedAt().toString() : null)
                .build();
    }

    /**
     * Mapea quién registró la entrada.
     */
    private static String mapRegisteredBy(LogEntry.RegisteredBy registeredBy) {
        if (registeredBy == null) return "ADMINISTRADOR";
        return switch (registeredBy) {
            case GODPARENT -> "PADRINO";
            case ADMIN -> "ADMINISTRADOR";
        };
    }
}

