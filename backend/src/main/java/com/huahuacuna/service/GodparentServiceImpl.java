package com.huahuacuna.service;

import com.huahuacuna.model.*;
import com.huahuacuna.model.dto.*;
import com.huahuacuna.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de padrinos.
 *
 * CORRECCIÓN v2.0:
 * - Removido el método addLogEntry para padrinos
 * - El padrino solo puede LEER la bitácora
 * - Agregados métodos addLogEntryByAdmin y getLogEntriesAdmin para administradores
 *
 * @author Fundación Huahuacuna
 * @version 2.0 - Solo lectura de bitácora para padrinos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GodparentServiceImpl implements GodparentService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final SponsorshipRepository sponsorshipRepository;
    private final LogEntryRepository logEntryRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;

    // ========== PERFIL DEL PADRINO ==========

    @Override
    @Transactional(readOnly = true)
    public GodparentProfileDTO getMyProfile(Long userId) {
        log.info("Obteniendo perfil del padrino con ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que sea un padrino
        if (user.getRole() != Role.PADRINO) {
            throw new RuntimeException("El usuario no es un padrino");
        }

        // Buscar apadrinamiento activo
        var sponsorship = sponsorshipRepository
                .findByGodparentIdAndStatus(userId, SponsorshipStatus.ACTIVE);

        if (sponsorship.isPresent()) {
            Sponsorship s = sponsorship.get();
            return GodparentProfileDTO.fromUserWithSponsorship(
                    user,
                    ChildResponseDTO.fromEntity(s.getChild()),
                    s.getStatus().getDisplayName()
            );
        }

        return GodparentProfileDTO.fromUser(user);
    }

    // ========== NIÑOS DISPONIBLES ==========

    @Override
    @Transactional(readOnly = true)
    public List<ChildResponseDTO> getAvailableChildren() {
        log.info("Obteniendo lista de niños disponibles");

        return childRepository.findByStatus(ChildStatus.AVAILABLE)
                .stream()
                .map(ChildResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== APADRINAMIENTO ==========

    @Override
    public SponsorshipResponseDTO selectChild(Long godparentId, Long childId) {
        log.info("Padrino {} seleccionando niño {}", godparentId, childId);

        // Validar que el padrino existe y es válido
        User godparent = userRepository.findById(godparentId)
                .orElseThrow(() -> new RuntimeException("Padrino no encontrado"));

        if (godparent.getRole() != Role.PADRINO) {
            throw new RuntimeException("El usuario no tiene rol de padrino");
        }

        // Validar que no tenga ya un apadrinamiento activo
        if (sponsorshipRepository.existsByGodparentIdAndStatus(godparentId, SponsorshipStatus.ACTIVE)) {
            throw new RuntimeException("Ya tienes un apadrinamiento activo");
        }

        // Validar que el niño existe y está disponible
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Niño no encontrado"));

        if (child.getStatus() != ChildStatus.AVAILABLE) {
            throw new RuntimeException("El niño no está disponible para apadrinamiento");
        }

        // Validar que el niño no esté ya apadrinado
        if (sponsorshipRepository.existsByChildIdAndStatus(childId, SponsorshipStatus.ACTIVE)) {
            throw new RuntimeException("El niño ya tiene un padrino activo");
        }

        // Crear el apadrinamiento
        Sponsorship sponsorship = Sponsorship.builder()
                .godparent(godparent)
                .child(child)
                .status(SponsorshipStatus.ACTIVE)
                .build();

        sponsorship = sponsorshipRepository.save(sponsorship);

        // Actualizar estado del niño
        child.setStatus(ChildStatus.SPONSORED);
        childRepository.save(child);

        // Crear notificación para todos los administradores
        try {
            notificationService.createNotificationForAllAdmins(
                    "Nuevo Apadrinamiento",
                    String.format("%s ha seleccionado apadrinar a %s %s",
                            godparent.getFullName(),
                            child.getFirstName(),
                            child.getLastName()),
                    "SUCCESS",
                    sponsorship.getId()
            );
        } catch (Exception e) {
            log.warn("No se pudo crear la notificación: {}", e.getMessage());
        }

        log.info("Apadrinamiento creado exitosamente: {}", sponsorship.getId());

        return SponsorshipResponseDTO.fromEntity(sponsorship);
    }

    @Override
    @Transactional(readOnly = true)
    public SponsorshipResponseDTO getMyGodchild(Long godparentId) {
        log.info("Obteniendo apadrinamiento activo del padrino: {}", godparentId);

        return sponsorshipRepository
                .findByGodparentIdAndStatusWithDetails(godparentId, SponsorshipStatus.ACTIVE)
                .map(SponsorshipResponseDTO::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSponsorship(Long godparentId) {
        return sponsorshipRepository.existsByGodparentIdAndStatus(godparentId, SponsorshipStatus.ACTIVE);
    }

    // ========== BITÁCORA (SOLO LECTURA PARA PADRINO) ==========

    @Override
    @Transactional(readOnly = true)
    public List<LogEntryDTO> getLogEntries(Long sponsorshipId, Long godparentId) {
        log.info("Obteniendo bitácora del apadrinamiento: {} para padrino: {}", sponsorshipId, godparentId);

        // Validar que el apadrinamiento pertenece al padrino
        validateSponsorshipOwnership(sponsorshipId, godparentId);

        return logEntryRepository.findBySponsorshipIdOrderByCreatedAtDesc(sponsorshipId)
                .stream()
                .map(LogEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ❌ REMOVIDO: addLogEntry para padrinos
     *
     * Este método ha sido ELIMINADO porque el padrino NO debe poder
     * agregar entradas a la bitácora. Esta funcionalidad es EXCLUSIVA
     * del administrador.
     *
     * El método addLogEntryByAdmin() debe usarse en su lugar desde el AdminController.
     */
    // @Override
    // public LogEntryDTO addLogEntry(Long sponsorshipId, Long godparentId, String title, String content) {
    //     // REMOVIDO - Solo el admin puede agregar entradas
    // }

    // ========== BITÁCORA (MÉTODOS PARA ADMIN) ==========

    @Override
    public LogEntryDTO addLogEntryByAdmin(Long sponsorshipId, String title, String content) {
        log.info("ADMIN agregando entrada a bitácora del apadrinamiento: {}", sponsorshipId);

        // Obtener el apadrinamiento (sin validar pertenencia a padrino)
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new RuntimeException("Apadrinamiento no encontrado"));

        // Crear la entrada de bitácora
        LogEntry entry = LogEntry.builder()
                .sponsorship(sponsorship)
                .title(title)
                .content(content)
                .entryType(LogEntry.LogEntryType.GENERAL)
                .registeredBy(LogEntry.RegisteredBy.ADMIN) // Registrado por ADMIN
                .createdByUserId(null) // No asociamos a un usuario específico
                .build();

        entry = logEntryRepository.save(entry);
        log.info("Entrada de bitácora creada por ADMIN: {}", entry.getId());



        return LogEntryDTO.fromEntity(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogEntryDTO> getLogEntriesAdmin(Long sponsorshipId) {
        log.info("ADMIN obteniendo bitácora del apadrinamiento: {}", sponsorshipId);

        // Validar que el apadrinamiento existe
        if (!sponsorshipRepository.existsById(sponsorshipId)) {
            throw new RuntimeException("Apadrinamiento no encontrado");
        }

        return logEntryRepository.findBySponsorshipIdOrderByCreatedAtDesc(sponsorshipId)
                .stream()
                .map(LogEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== CHAT ==========

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(Long sponsorshipId, Long godparentId) {
        log.info("Obteniendo mensajes del apadrinamiento: {}", sponsorshipId);

        // Validar que el apadrinamiento pertenece al padrino
        validateSponsorshipOwnership(sponsorshipId, godparentId);

        return chatMessageRepository.findBySponsorshipIdOrderByCreatedAtAsc(sponsorshipId)
                .stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDTO sendMessage(Long sponsorshipId, Long godparentId, String content) {
        log.info("Enviando mensaje en apadrinamiento: {}", sponsorshipId);

        // Validar que el apadrinamiento pertenece al padrino
        Sponsorship sponsorship = validateSponsorshipOwnership(sponsorshipId, godparentId);

        ChatMessage message = ChatMessage.builder()
                .sponsorship(sponsorship)
                .content(content)
                .sentBy(ChatMessage.SentBy.GODPARENT)
                .senderUserId(godparentId)
                .isRead(false)
                .build();

        message = chatMessageRepository.save(message);
        log.info("Mensaje enviado: {}", message.getId());

        // Notificar a todos los administradores
        try {
            notificationService.createNotificationForAllAdmins(
                    "Nuevo mensaje de padrino",
                    String.format("Tienes un nuevo mensaje de %s",
                            sponsorship.getGodparent().getFullName()),
                    "INFO",
                    sponsorshipId
            );
        } catch (Exception e) {
            log.warn("No se pudo crear la notificación: {}", e.getMessage());
        }

        return ChatMessageDTO.fromEntity(message);
    }

    @Override
    public void markMessagesAsRead(Long sponsorshipId, Long godparentId) {
        log.info("Marcando mensajes como leídos en apadrinamiento: {}", sponsorshipId);

        // Validar que el apadrinamiento pertenece al padrino
        validateSponsorshipOwnership(sponsorshipId, godparentId);

        // Marcar como leídos los mensajes del admin
        chatMessageRepository.markAsReadBySentBy(sponsorshipId, ChatMessage.SentBy.ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessages(Long godparentId) {
        return chatMessageRepository.countUnreadForGodparent(godparentId);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Valida que el apadrinamiento pertenece al padrino especificado.
     * @param sponsorshipId ID del apadrinamiento
     * @param godparentId ID del padrino
     * @return El apadrinamiento si es válido
     * @throws RuntimeException si no es válido
     */
    private Sponsorship validateSponsorshipOwnership(Long sponsorshipId, Long godparentId) {
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new RuntimeException("Apadrinamiento no encontrado"));

        if (!sponsorship.getGodparent().getId().equals(godparentId)) {
            throw new RuntimeException("No tienes permiso para acceder a este apadrinamiento");
        }

        return sponsorship;
    }
    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipSummaryDTO> getAllActiveSponsorshipsForAdmin() {
        log.info("ADMIN obteniendo todos los apadrinamientos activos");

        List<Sponsorship> sponsorships = sponsorshipRepository.findByStatus(SponsorshipStatus.ACTIVE);

        return sponsorships.stream()
                .map(sponsorship -> {
                    // Contar entradas de bitácora para cada apadrinamiento
                    Long entriesCount = logEntryRepository.countBySponsorshipId(sponsorship.getId());
                    return SponsorshipSummaryDTO.fromEntity(sponsorship, entriesCount);
                })
                .collect(Collectors.toList());
    }
}