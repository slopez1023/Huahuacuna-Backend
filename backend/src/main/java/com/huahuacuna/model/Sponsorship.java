package com.huahuacuna.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un apadrinamiento.
 * Vincula un usuario (padrino) con un niño de la fundación.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Entity
@Table(name = "sponsorships", indexes = {
        @Index(name = "idx_sponsorship_godparent", columnList = "godparent_id"),
        @Index(name = "idx_sponsorship_child", columnList = "child_id"),
        @Index(name = "idx_sponsorship_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sponsorship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que actúa como padrino.
     * Relación muchos-a-uno con User.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "godparent_id", nullable = false)
    private User godparent;

    /**
     * Niño apadrinado.
     * Relación muchos-a-uno con Child.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    /**
     * Estado actual del apadrinamiento.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SponsorshipStatus status = SponsorshipStatus.ACTIVE;

    /**
     * Fecha de inicio del apadrinamiento.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * Fecha de finalización del apadrinamiento (si aplica).
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Notas adicionales sobre el apadrinamiento.
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Fecha de creación del registro.
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
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = SponsorshipStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el apadrinamiento está activo.
     */
    public boolean isActive() {
        return this.status == SponsorshipStatus.ACTIVE;
    }

    /**
     * Finaliza el apadrinamiento.
     */
    public void end() {
        this.status = SponsorshipStatus.ENDED;
        this.endDate = LocalDateTime.now();
    }

    /**
     * Pausa el apadrinamiento.
     */
    public void pause() {
        this.status = SponsorshipStatus.PAUSED;
    }

    /**
     * Reactiva el apadrinamiento.
     */
    public void reactivate() {
        this.status = SponsorshipStatus.ACTIVE;
        this.endDate = null;
    }
}