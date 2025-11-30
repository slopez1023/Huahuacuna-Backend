package com.huahuacuna.model.dto;

import com.huahuacuna.model.Sponsorship;
import com.huahuacuna.model.SponsorshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de apadrinamiento.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SponsorshipResponseDTO {

    private Long id;
    private GodparentProfileDTO padrino;
    private ChildResponseDTO nino;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
    private String notas;

    /**
     * Convierte una entidad Sponsorship a DTO.
     */
    public static SponsorshipResponseDTO fromEntity(Sponsorship sponsorship) {
        if (sponsorship == null) return null;

        return SponsorshipResponseDTO.builder()
                .id(sponsorship.getId())
                .padrino(GodparentProfileDTO.fromUser(sponsorship.getGodparent()))
                .nino(ChildResponseDTO.fromEntity(sponsorship.getChild()))
                .estado(mapStatus(sponsorship.getStatus()))
                .fechaInicio(sponsorship.getStartDate() != null ?
                        sponsorship.getStartDate().toString() : null)
                .fechaFin(sponsorship.getEndDate() != null ?
                        sponsorship.getEndDate().toString() : null)
                .notas(sponsorship.getNotes())
                .build();
    }

    /**
     * Mapea el estado del apadrinamiento.
     */
    private static String mapStatus(SponsorshipStatus status) {
        if (status == null) return "ACTIVO";
        return switch (status) {
            case ACTIVE -> "ACTIVO";
            case PAUSED -> "PAUSADO";
            case ENDED -> "TERMINADO";
        };
    }
}