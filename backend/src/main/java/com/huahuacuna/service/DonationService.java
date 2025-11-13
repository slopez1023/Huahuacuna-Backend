package com.huahuacuna.service;

import com.huahuacuna.model.Donation;
import com.huahuacuna.model.DonationRequest;
import com.huahuacuna.model.DonationStatsDTO;
import com.huahuacuna.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

    @Transactional
    public Donation createDonation(DonationRequest request) {
        Donation donation = new Donation();
        donation.setAmount(request.getAmount());
        donation.setDonorName(request.getFullName());
        donation.setEmail(request.getEmail());
        donation.setPhone(request.getPhone());
        donation.setDonationType(request.getDonationType());
        donation.setPaymentMethod(request.getPaymentMethod());
        donation.setDescription(request.getDescription());
        donation.setItemType(request.getItemType());
        donation.setStatus("pending");

        return donationRepository.save(donation);
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
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada"));
        donation.setStatus(status);
        return donationRepository.save(donation);
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