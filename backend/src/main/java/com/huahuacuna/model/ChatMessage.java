package com.huahuacuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje en el chat entre padrino y administrador.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_sponsorship", columnList = "sponsorship_id"),
        @Index(name = "idx_chat_created_at", columnList = "created_at"),
        @Index(name = "idx_chat_is_read", columnList = "is_read")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Apadrinamiento al que pertenece este mensaje.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_id", nullable = false)
    private Sponsorship sponsorship;

    /**
     * Contenido del mensaje.
     */
    @NotBlank(message = "El mensaje no puede estar vacío")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Quién envió el mensaje.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sent_by", nullable = false, length = 20)
    private SentBy sentBy;

    /**
     * ID del usuario que envió el mensaje.
     */
    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    /**
     * Indica si el mensaje ha sido leído.
     */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Fecha y hora en que se leyó el mensaje.
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Fecha de creación del mensaje.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
    }

    /**
     * Marca el mensaje como leído.
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Quién envió el mensaje.
     */
    public enum SentBy {
        GODPARENT("Padrino"),
        ADMIN("Administrador");

        private final String displayName;

        SentBy(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}