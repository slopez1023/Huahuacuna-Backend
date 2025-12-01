package com.huahuacuna.model.dto;

import com.huahuacuna.model.Sponsorship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para mostrar resumen de apadrinamientos en el panel de admin.
 * Usado principalmente en la sección de gestión de bitácoras.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorshipSummaryDTO {

    private Long id;
    private String godparentName;
    private String godparentEmail;
    private String childName;
    private Long childId;
    private String childImageUrl;
    private String status;
    private LocalDateTime createdAt;
    private Long entriesCount;

    /**
     * Convierte una entidad Sponsorship a DTO con conteo de entradas.
     *
     * @param sponsorship La entidad de apadrinamiento
     * @param entriesCount Número de entradas en la bitácora
     * @return SponsorshipSummaryDTO
     */
    public static SponsorshipSummaryDTO fromEntity(Sponsorship sponsorship, Long entriesCount) {
        return SponsorshipSummaryDTO.builder()
                .id(sponsorship.getId())
                .godparentName(sponsorship.getGodparent().getFullName())
                .godparentEmail(sponsorship.getGodparent().getEmail())
                .childName(sponsorship.getChild().getFirstName() + " " + sponsorship.getChild().getLastName())
                .childId(sponsorship.getChild().getId())
                .childImageUrl(sponsorship.getChild().getImageUrl())
                .status(sponsorship.getStatus().name())
                .createdAt(sponsorship.getCreatedAt())
                .entriesCount(entriesCount)
                .build();
    }

    /**
     * Convierte una entidad Sponsorship a DTO sin conteo de entradas.
     *
     * @param sponsorship La entidad de apadrinamiento
     * @return SponsorshipSummaryDTO
     */
    public static SponsorshipSummaryDTO fromEntity(Sponsorship sponsorship) {
        return fromEntity(sponsorship, 0L);
    }
}