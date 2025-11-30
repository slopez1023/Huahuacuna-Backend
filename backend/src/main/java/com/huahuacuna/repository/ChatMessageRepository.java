package com.huahuacuna.repository;

import com.huahuacuna.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar mensajes del chat.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Encuentra todos los mensajes de un apadrinamiento ordenados por fecha.
     */
    List<ChatMessage> findBySponsorshipIdOrderByCreatedAtAsc(Long sponsorshipId);

    /**
     * Encuentra mensajes con paginación (más recientes primero).
     */
    Page<ChatMessage> findBySponsorshipIdOrderByCreatedAtDesc(Long sponsorshipId, Pageable pageable);

    /**
     * Encuentra mensajes no leídos de un apadrinamiento.
     */
    List<ChatMessage> findBySponsorshipIdAndIsReadFalseOrderByCreatedAtAsc(Long sponsorshipId);

    /**
     * Encuentra mensajes no leídos enviados por un tipo de usuario.
     */
    List<ChatMessage> findBySponsorshipIdAndSentByAndIsReadFalse(
            Long sponsorshipId,
            ChatMessage.SentBy sentBy
    );

    /**
     * Cuenta mensajes no leídos de un apadrinamiento.
     */
    long countBySponsorshipIdAndIsReadFalse(Long sponsorshipId);

    /**
     * Cuenta mensajes no leídos enviados por padrinos (para el admin).
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.sentBy = 'GODPARENT' AND m.isRead = false")
    long countUnreadFromGodparents();

    /**
     * Cuenta mensajes no leídos para un padrino específico.
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.sponsorship.godparent.id = :godparentId " +
            "AND m.sentBy = 'ADMIN' AND m.isRead = false")
    long countUnreadForGodparent(@Param("godparentId") Long godparentId);

    /**
     * Marca todos los mensajes de un apadrinamiento como leídos.
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP " +
            "WHERE m.sponsorship.id = :sponsorshipId AND m.isRead = false")
    int markAllAsRead(@Param("sponsorshipId") Long sponsorshipId);

    /**
     * Marca como leídos los mensajes enviados por un tipo de usuario.
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP " +
            "WHERE m.sponsorship.id = :sponsorshipId " +
            "AND m.sentBy = :sentBy AND m.isRead = false")
    int markAsReadBySentBy(
            @Param("sponsorshipId") Long sponsorshipId,
            @Param("sentBy") ChatMessage.SentBy sentBy
    );

    /**
     * Encuentra los últimos mensajes de cada apadrinamiento (para vista admin).
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.createdAt = (" +
            "  SELECT MAX(m2.createdAt) FROM ChatMessage m2 " +
            "  WHERE m2.sponsorship.id = m.sponsorship.id" +
            ") ORDER BY m.createdAt DESC")
    List<ChatMessage> findLatestMessagePerSponsorship();

    /**
     * Encuentra apadrinamientos con mensajes no leídos.
     */
    @Query("SELECT DISTINCT m.sponsorship.id FROM ChatMessage m " +
            "WHERE m.isRead = false AND m.sentBy = 'GODPARENT'")
    List<Long> findSponsorshipsWithUnreadMessages();
}