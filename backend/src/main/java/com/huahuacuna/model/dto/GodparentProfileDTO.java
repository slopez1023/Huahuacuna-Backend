package com.huahuacuna.model.dto;

import com.huahuacuna.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el perfil del padrino.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GodparentProfileDTO {

    private Long id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String pais;
    private String numeroDocumento;
    private String estado;
    private String fechaRegistro;
    private ChildResponseDTO ninoApadrinado;
    private String estadoApadrinamiento;
    private String fechaAprobacion;

    /**
     * Crea un DTO desde un User (padrino).
     */
    public static GodparentProfileDTO fromUser(User user) {
        if (user == null) return null;

        return GodparentProfileDTO.builder()
                .id(user.getId())
                .nombreCompleto(user.getFullName())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .pais(null) // Se puede obtener de ApplicationRequest si existe
                .numeroDocumento(null) // Se puede obtener de ApplicationRequest
                .estado(user.getIsActive() ? "ACTIVO" : "INACTIVO")
                .fechaRegistro(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }

    /**
     * Crea un DTO completo con información del apadrinamiento.
     */
    public static GodparentProfileDTO fromUserWithSponsorship(
            User user,
            ChildResponseDTO child,
            String sponsorshipStatus
    ) {
        GodparentProfileDTO dto = fromUser(user);
        if (dto != null) {
            dto.setNinoApadrinado(child);
            dto.setEstadoApadrinamiento(sponsorshipStatus);
        }
        return dto;
    }
}