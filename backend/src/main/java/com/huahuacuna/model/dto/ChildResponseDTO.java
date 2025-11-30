package com.huahuacuna.model.dto;

import com.huahuacuna.model.Child;
import com.huahuacuna.model.ChildStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

/**
 * DTO para respuestas de información de niños.
 * Usado para exponer datos de niños al frontend.
 *
 * @author Fundación Huahuacuna
 * @version 2.0 - Agregado campo gender y needs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private Integer edad;
    private String genero;
    private String biografia;
    private String fotoUrl;
    private String necesidades;
    private String estado;
    private String fechaRegistro;
    private String fechaActualizacion;

    /**
     * Convierte una entidad Child a DTO.
     */
    public static ChildResponseDTO fromEntity(Child child) {
        if (child == null) return null;

        int age = 0;
        if (child.getBirthDate() != null) {
            age = Period.between(child.getBirthDate(), LocalDate.now()).getYears();
        }

        return ChildResponseDTO.builder()
                .id(child.getId())
                .nombre(child.getFirstName())
                .apellido(child.getLastName())
                .fechaNacimiento(child.getBirthDate() != null ? child.getBirthDate().toString() : null)
                .edad(age)
                .genero(mapGender(child.getGender()))
                .biografia(child.getStory())
                .fotoUrl(child.getImageUrl())
                .necesidades(child.getNeeds())
                .estado(mapStatus(child.getStatus()))
                .fechaRegistro(null)
                .fechaActualizacion(null)
                .build();
    }

    /**
     * Mapea el estado del niño al formato esperado por el frontend.
     */
    private static String mapStatus(ChildStatus status) {
        if (status == null) return "DISPONIBLE";
        return switch (status) {
            case AVAILABLE -> "DISPONIBLE";
            case SPONSORED -> "APADRINADO";
            case INACTIVE -> "INACTIVO";
        };
    }

    /**
     * Mapea el género al formato legible.
     */
    private static String mapGender(String gender) {
        if (gender == null || gender.isBlank()) return "No especificado";
        return switch (gender.toUpperCase()) {
            case "MASCULINO", "M", "MALE" -> "Masculino";
            case "FEMENINO", "F", "FEMALE" -> "Femenino";
            default -> gender;
        };
    }
}