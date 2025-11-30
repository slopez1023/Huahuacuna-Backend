package com.huahuacuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una entrada en la bitácora de un apadrinamiento.
 * Permite registrar el progreso, novedades y eventos importantes del niño.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Entity
@Table(name = "log_entries", indexes = {
        @Index(name = "idx_log_sponsorship", columnList = "sponsorship_id"),
        @Index(name = "idx_log_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Apadrinamiento al que pertenece esta entrada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_id", nullable = false)
    private Sponsorship sponsorship;

    /**
     * Título de la entrada.
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Contenido de la entrada.
     */
    @NotBlank(message = "El contenido es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Tipo de entrada en la bitácora.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private LogEntryType entryType = LogEntryType.GENERAL;

    /**
     * Quién registró la entrada (GODPARENT o ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "registered_by", nullable = false, length = 20)
    private RegisteredBy registeredBy;

    /**
     * Usuario que creó la entrada (referencia al ID).
     */
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    /**
     * Fecha de creación de la entrada.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.entryType == null) {
            this.entryType = LogEntryType.GENERAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tipos de entrada en la bitácora.
     */
    public enum LogEntryType {
        GENERAL("General"),
        ACADEMIC("Académico"),
        HEALTH("Salud"),
        BEHAVIOR("Comportamiento"),
        ACHIEVEMENT("Logro"),
        EVENT("Evento"),
        NOTE("Nota");

        private final String displayName;

        LogEntryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Quién registró la entrada.
     */
    public enum RegisteredBy {
        GODPARENT("Padrino"),
        ADMIN("Administrador");

        private final String displayName;

        RegisteredBy(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}