package com.huahuacuna.controller;

import com.huahuacuna.model.ChatMessage;
import com.huahuacuna.model.Sponsorship;
import com.huahuacuna.model.SponsorshipStatus;
import com.huahuacuna.model.dto.ChatMessageDTO;
import com.huahuacuna.repository.ChatMessageRepository;
import com.huahuacuna.repository.SponsorshipRepository;
import com.huahuacuna.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para el chat del administrador con padrinos.
 * Base path: /api/admin/chat
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class AdminChatController {

    private final SponsorshipRepository sponsorshipRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtService jwtService;

    /**
     * Obtiene todas las conversaciones con padrinos.
     * GET /api/admin/chat/conversations
     */
    @GetMapping("/conversations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getConversations() {
        log.info("GET /api/admin/chat/conversations");

        // Obtener todos los apadrinamientos activos
        List<Sponsorship> sponsorships = sponsorshipRepository.findByStatusWithDetails(SponsorshipStatus.ACTIVE);

        List<Map<String, Object>> conversations = new ArrayList<>();

        for (Sponsorship s : sponsorships) {
            // Obtener mensajes de esta conversación
            List<ChatMessage> messages = chatMessageRepository.findBySponsorshipIdOrderByCreatedAtAsc(s.getId());

            // Solo incluir si hay mensajes
            if (!messages.isEmpty()) {
                ChatMessage lastMessage = messages.get(messages.size() - 1);

                // Contar mensajes no leídos del padrino
                long unreadCount = messages.stream()
                        .filter(m -> m.getSentBy() == ChatMessage.SentBy.GODPARENT && !m.getIsRead())
                        .count();

                Map<String, Object> conversation = new HashMap<>();
                conversation.put("sponsorshipId", s.getId());
                conversation.put("godparentId", s.getGodparent().getId());
                conversation.put("godparentName", s.getGodparent().getFullName());
                conversation.put("godparentEmail", s.getGodparent().getEmail());
                conversation.put("childId", s.getChild().getId());
                conversation.put("childName", s.getChild().getFirstName() + " " + s.getChild().getLastName());
                conversation.put("lastMessage", lastMessage.getContent());
                conversation.put("lastMessageAt", lastMessage.getCreatedAt().toString());
                conversation.put("unreadCount", unreadCount);

                conversations.add(conversation);
            }
        }

        // Ordenar por último mensaje (más reciente primero)
        conversations.sort((a, b) -> {
            String dateA = (String) a.get("lastMessageAt");
            String dateB = (String) b.get("lastMessageAt");
            return dateB.compareTo(dateA);
        });

        return ResponseEntity.ok(conversations);
    }

    /**
     * Obtiene los mensajes de una conversación específica.
     * GET /api/admin/chat/conversations/{sponsorshipId}/messages
     */
    @GetMapping("/conversations/{sponsorshipId}/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long sponsorshipId) {
        log.info("GET /api/admin/chat/conversations/{}/messages", sponsorshipId);

        List<ChatMessage> messages = chatMessageRepository.findBySponsorshipIdOrderByCreatedAtAsc(sponsorshipId);

        List<ChatMessageDTO> dtos = messages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Envía un mensaje como administrador.
     * POST /api/admin/chat/conversations/{sponsorshipId}/messages
     */
    @PostMapping("/conversations/{sponsorshipId}/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long sponsorshipId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        Long adminId = extractUserIdFromToken(request);
        String contenido = body.get("contenido");

        log.info("POST /api/admin/chat/conversations/{}/messages - Admin: {}", sponsorshipId, adminId);

        if (contenido == null || contenido.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El mensaje no puede estar vacío"
            ));
        }

        // Verificar que el apadrinamiento existe
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElse(null);

        if (sponsorship == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Apadrinamiento no encontrado"
            ));
        }

        // Crear mensaje
        ChatMessage message = ChatMessage.builder()
                .sponsorship(sponsorship)
                .content(contenido)
                .sentBy(ChatMessage.SentBy.ADMIN)
                .senderUserId(adminId)
                .isRead(false)
                .build();

        message = chatMessageRepository.save(message);

        log.info("Mensaje enviado por admin: {}", message.getId());

        return ResponseEntity.ok(toDTO(message));
    }

    /**
     * Marca los mensajes de una conversación como leídos.
     * PUT /api/admin/chat/conversations/{sponsorshipId}/read
     */
    @PutMapping("/conversations/{sponsorshipId}/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long sponsorshipId) {
        log.info("PUT /api/admin/chat/conversations/{}/read", sponsorshipId);

        // Marcar como leídos los mensajes del padrino
        chatMessageRepository.markAsReadBySentBy(sponsorshipId, ChatMessage.SentBy.GODPARENT);

        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * Obtiene el conteo total de mensajes no leídos.
     * GET /api/admin/chat/unread-count
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        log.info("GET /api/admin/chat/unread-count");

        long count = chatMessageRepository.countUnreadFromGodparents();

        return ResponseEntity.ok(Map.of("count", count));
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Long extractUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        return jwtService.getUserIdFromToken(token);
    }

    private ChatMessageDTO toDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .apadrinamientoId(message.getSponsorship().getId())
                .contenido(message.getContent())
                .enviadoPor(message.getSentBy() == ChatMessage.SentBy.GODPARENT ? "PADRINO" : "ADMINISTRADOR")
                .fecha(message.getCreatedAt().toString())
                .leido(message.getIsRead())
                .fechaLectura(message.getReadAt() != null ? message.getReadAt().toString() : null)
                .build();
    }
}