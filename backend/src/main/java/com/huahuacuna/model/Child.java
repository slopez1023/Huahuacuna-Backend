package com.huahuacuna.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.Period;

/**
 * Entidad que representa a un niño en el sistema.
 *
 * @author Fundación Huahuacuna
 * @version 2.0 - Agregados campos gender y needs
 */
@Entity
@Table(name = "children")
@Data
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    /**
     * Género del niño: MASCULINO, FEMENINO
     */
    @Column(length = 20)
    private String gender;

    /**
     * Calcula la edad automáticamente, no se guarda en BD
     */
    @Transient
    public int getAge() {
        if (this.birthDate == null) return 0;
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    /**
     * Historia/biografía del niño
     */
    @Column(columnDefinition = "TEXT")
    private String story;

    /**
     * URL de la foto del niño
     */
    private String imageUrl;

    /**
     * Necesidades especiales del niño
     */
    @Column(columnDefinition = "TEXT")
    private String needs;

    /**
     * Estado del niño: AVAILABLE, SPONSORED, INACTIVE
     */
    @Enumerated(EnumType.STRING)
    private ChildStatus status = ChildStatus.AVAILABLE;
}