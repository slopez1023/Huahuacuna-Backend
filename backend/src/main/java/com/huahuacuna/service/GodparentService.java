package com.huahuacuna.service;

import com.huahuacuna.model.dto.*;

import java.util.List;

/**
 * Servicio para gestionar funcionalidades de padrinos.
 *
 * CORRECCIÓN v2.0:
 * - Removido el método addLogEntry para padrinos
 * - El padrino solo puede LEER la bitácora
 * - Agregados métodos para que el ADMIN gestione la bitácora
 *
 * @author Fundación Huahuacuna
 * @version 2.0 - Solo lectura de bitácora para padrinos
 */
public interface GodparentService {

    // ========== PERFIL DEL PADRINO ==========

    /**
     * Obtiene el perfil del padrino actual (usuario logueado).
     * @param userId ID del usuario autenticado
     * @return Perfil del padrino
     */
    GodparentProfileDTO getMyProfile(Long userId);

    // ========== NIÑOS DISPONIBLES ==========

    /**
     * Obtiene la lista de niños disponibles para apadrinar.
     * @return Lista de niños disponibles
     */
    List<ChildResponseDTO> getAvailableChildren();

    // ========== APADRINAMIENTO ==========

    /**
     * Selecciona un niño para apadrinar.
     * @param godparentId ID del padrino
     * @param childId ID del niño
     * @return Información del apadrinamiento creado
     */
    SponsorshipResponseDTO selectChild(Long godparentId, Long childId);

    /**
     * Obtiene el apadrinamiento activo del padrino.
     * @param godparentId ID del padrino
     * @return Información del apadrinamiento o null si no tiene
     */
    SponsorshipResponseDTO getMyGodchild(Long godparentId);

    /**
     * Verifica si el padrino ya tiene un apadrinamiento activo.
     * @param godparentId ID del padrino
     * @return true si ya tiene apadrinamiento activo
     */
    boolean hasActiveSponsorship(Long godparentId);

    // ========== BITÁCORA (SOLO LECTURA PARA PADRINO) ==========

    /**
     * Obtiene las entradas de bitácora de un apadrinamiento.
     * El padrino solo puede LEER las entradas, no crearlas.
     *
     * @param sponsorshipId ID del apadrinamiento
     * @param godparentId ID del padrino (para validación de pertenencia)
     * @return Lista de entradas de bitácora
     */
    List<LogEntryDTO> getLogEntries(Long sponsorshipId, Long godparentId);

    /**
     * ❌ REMOVIDO: addLogEntry para padrinos
     * El padrino NO debe poder agregar entradas a la bitácora.
     * Esta funcionalidad es EXCLUSIVA del administrador.
     *
     * Usar en su lugar: addLogEntryByAdmin()
     */
    // LogEntryDTO addLogEntry(Long sponsorshipId, Long godparentId, String title, String content);

    // ========== BITÁCORA (MÉTODOS PARA ADMIN) ==========

    /**
     * Agrega una entrada a la bitácora (SOLO ADMIN).
     * Este método no requiere validación de pertenencia del padrino.
     *
     * @param sponsorshipId ID del apadrinamiento
     * @param title Título de la entrada
     * @param content Contenido de la entrada
     * @return LogEntryDTO con la entrada creada
     */
    LogEntryDTO addLogEntryByAdmin(Long sponsorshipId, String title, String content);

    /**
     * Obtiene las entradas de bitácora para admin (sin validación de usuario).
     *
     * @param sponsorshipId ID del apadrinamiento
     * @return Lista de entradas de bitácora
     */
    List<LogEntryDTO> getLogEntriesAdmin(Long sponsorshipId);

    // ========== CHAT ==========

    /**
     * Obtiene los mensajes del chat de un apadrinamiento.
     * @param sponsorshipId ID del apadrinamiento
     * @param godparentId ID del padrino (para validación)
     * @return Lista de mensajes
     */
    List<ChatMessageDTO> getChatMessages(Long sponsorshipId, Long godparentId);

    /**
     * Envía un mensaje al administrador.
     * @param sponsorshipId ID del apadrinamiento
     * @param godparentId ID del padrino
     * @param content Contenido del mensaje
     * @return Mensaje enviado
     */
    ChatMessageDTO sendMessage(Long sponsorshipId, Long godparentId, String content);

    /**
     * Marca los mensajes del admin como leídos.
     * @param sponsorshipId ID del apadrinamiento
     * @param godparentId ID del padrino
     */
    void markMessagesAsRead(Long sponsorshipId, Long godparentId);

    /**
     * Cuenta mensajes no leídos para el padrino.
     * @param godparentId ID del padrino
     * @return Número de mensajes no leídos
     */
    long countUnreadMessages(Long godparentId);
    /**
     * Obtiene todos los apadrinamientos activos para el panel de admin.
     * Incluye información del niño, padrino y conteo de entradas de bitácora.
     *
     * @return Lista de SponsorshipSummaryDTO
     */
    List<SponsorshipSummaryDTO> getAllActiveSponsorshipsForAdmin();
}