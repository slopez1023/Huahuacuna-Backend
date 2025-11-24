package com.huahuacuna.service;

import com.huahuacuna.model.Donation;
import com.huahuacuna.model.DonationRequest;
import com.huahuacuna.model.dto.DonationStatsDTO;
import com.huahuacuna.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public Donation createDonation(DonationRequest request) {
        log.info("📝 Creando donación para: {}", request.getFullName());
        log.debug("📋 Datos recibidos: {}", request);

        // ✅ Validaciones
        validateDonationRequest(request);

        Donation donation = new Donation();
        donation.setAmount(request.getAmount() != null ? request.getAmount() : 0.0);
        donation.setDonorName(request.getFullName());
        donation.setEmail(request.getEmail());
        donation.setPhone(request.getPhone());
        donation.setDonationType(request.getDonationType());
        donation.setPaymentMethod(request.getPaymentMethod());
        donation.setDescription(request.getDescription());
        donation.setItemType(request.getItemType());
        donation.setStatus("pending");

        Donation saved = donationRepository.save(donation);
        log.info("✅ Donación guardada con ID: {}", saved.getId());

        // ✅ ENVIAR EMAIL DE CONFIRMACIÓN AL DONANTE
        try {
            if ("MONETARY".equalsIgnoreCase(saved.getDonationType())) {
                // Email para donación monetaria
                emailService.sendMonetaryDonationConfirmation(
                        saved.getEmail(),
                        saved.getDonorName(),
                        saved.getId(),
                        saved.getAmount(),
                        saved.getPaymentMethod()
                );
                log.info("📧 Email de confirmación de donación monetaria enviado a: {}", saved.getEmail());
            } else if ("IN_KIND".equalsIgnoreCase(saved.getDonationType())) {
                // Email para donación en especie
                emailService.sendInKindDonationConfirmation(
                        saved.getEmail(),
                        saved.getDonorName(),
                        saved.getId(),
                        saved.getItemType(),
                        saved.getDescription()
                );
                log.info("📧 Email de confirmación de donación en especie enviado a: {}", saved.getEmail());
            }
        } catch (Exception e) {
            log.error("⚠️ Error al enviar email de confirmación: {}", e.getMessage());
            // No lanzamos excepción para no interrumpir el proceso
        }

        // ✅ CREAR NOTIFICACIÓN PARA TODOS LOS ADMINISTRADORES
        try {
            String notificationTitle = "Nueva Donación Recibida";
            String notificationMessage;

            if ("MONETARY".equalsIgnoreCase(saved.getDonationType())) {
                notificationMessage = String.format(
                        "Se ha recibido una donación monetaria de $%,.0f COP por %s",
                        saved.getAmount(),
                        saved.getDonorName()
                );
            } else {
                notificationMessage = String.format(
                        "Se ha recibido una donación en especie (%s) de %s",
                        saved.getItemType() != null ? saved.getItemType() : "artículo",
                        saved.getDonorName()
                );
            }

            // Crear notificación para todos los administradores
            notificationService.createNotificationForAllAdmins(
                    notificationTitle,
                    notificationMessage,
                    "DONATION",      // ✅ String, no enum
                    saved.getId()    // ID de la donación
            );

            log.info("🔔 Notificaciones creadas para todos los administradores sobre la donación {}", saved.getId());
        } catch (Exception e) {
            // No lanzamos error si falla la notificación
            log.error("⚠️ Error al crear notificaciones: {}", e.getMessage(), e);
        }

        return saved;
    }

    // ✅ Método de validación
    private void validateDonationRequest(DonationRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }

        if ("MONETARY".equalsIgnoreCase(request.getDonationType())) {
            if (request.getAmount() == null || request.getAmount() < 1000) {
                throw new IllegalArgumentException("El monto mínimo es $1,000 COP");
            }
        } else if ("IN_KIND".equalsIgnoreCase(request.getDonationType())) {
            if (request.getItemType() == null || request.getItemType().isBlank()) {
                throw new IllegalArgumentException("Debe especificar el tipo de artículo");
            }
            if (request.getDescription() == null || request.getDescription().isBlank()) {
                throw new IllegalArgumentException("Debe proporcionar una descripción");
            }
        }
    }

    public List<Donation> getAllDonations(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String status,
            String type
    ) {
        if (startDate != null || endDate != null || status != null || type != null) {
            return donationRepository.findByFilters(startDate, endDate, status, type);
        }
        return donationRepository.findAll();
    }

    public DonationStatsDTO getDonationStats(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        DonationStatsDTO stats = new DonationStatsDTO();

        // Resumen general
        Long totalDonations = donationRepository.countDonations(startDate, endDate);
        Double totalAmount = donationRepository.sumTotalAmount(startDate, endDate);
        Double averageDonation = totalDonations > 0 ? totalAmount / totalDonations : 0.0;

        stats.setSummary(new DonationStatsDTO.SummaryStats(
                totalDonations,
                totalAmount,
                averageDonation
        ));

        // Por estado
        List<DonationStatsDTO.StatusCount> byStatus = donationRepository
                .countByStatus(startDate, endDate)
                .stream()
                .map(obj -> new DonationStatsDTO.StatusCount(
                        (String) obj[0],
                        ((Number) obj[1]).longValue(),
                        ((Number) obj[2]).doubleValue()
                ))
                .collect(Collectors.toList());
        stats.setByStatus(byStatus);

        // Por tipo
        List<DonationStatsDTO.TypeCount> byType = donationRepository
                .countByType(startDate, endDate)
                .stream()
                .map(obj -> new DonationStatsDTO.TypeCount(
                        (String) obj[0],
                        ((Number) obj[1]).longValue(),
                        ((Number) obj[2]).doubleValue()
                ))
                .collect(Collectors.toList());
        stats.setByType(byType);

        // Por método de pago
        List<DonationStatsDTO.PaymentMethodCount> byPaymentMethod = donationRepository
                .countByPaymentMethod(startDate, endDate)
                .stream()
                .map(obj -> new DonationStatsDTO.PaymentMethodCount(
                        (String) obj[0],
                        ((Number) obj[1]).longValue(),
                        ((Number) obj[2]).doubleValue()
                ))
                .collect(Collectors.toList());
        stats.setByPaymentMethod(byPaymentMethod);

        // Por mes
        List<Donation> donations = donationRepository.findByFilters(startDate, endDate, null, null);
        Map<String, DonationStatsDTO.MonthlyStats> byMonth = donations.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new DonationStatsDTO.MonthlyStats(
                                        (long) list.size(),
                                        list.stream()
                                                .mapToDouble(d -> d.getAmount() != null ? d.getAmount() : 0.0)
                                                .sum()
                                )
                        )
                ));
        stats.setByMonth(byMonth);

        // Top donantes
        List<DonationStatsDTO.TopDonor> topDonors = donationRepository
                .findTopDonors(startDate, endDate)
                .stream()
                .limit(10)
                .map(obj -> new DonationStatsDTO.TopDonor(
                        (String) obj[0],
                        (String) obj[1],
                        ((Number) obj[2]).doubleValue(),
                        ((Number) obj[3]).longValue()
                ))
                .collect(Collectors.toList());
        stats.setTopDonors(topDonors);

        return stats;
    }

    @Transactional
    public Donation updateDonationStatus(Long id, String status) {
        log.info("🔄 Actualizando estado de donación {} a: {}", id, status);

        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada con ID: " + id));
        donation.setStatus(status);

        Donation updated = donationRepository.save(donation);
        log.info("✅ Estado actualizado correctamente");

        return updated;
    }

    public String exportToCSV(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<Donation> donations = donationRepository.findByFilters(startDate, endDate, null, null);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Fecha,Donante,Email,Teléfono,Tipo,Monto,Método de Pago,Estado\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Donation d : donations) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    d.getId(),
                    d.getCreatedAt().format(formatter),
                    d.getDonorName(),
                    d.getEmail(),
                    d.getPhone(),
                    d.getDonationType(),
                    d.getAmount() != null ? d.getAmount() : "",
                    d.getPaymentMethod() != null ? d.getPaymentMethod() : "",
                    d.getStatus()
            ));
        }

        return csv.toString();
    }
}