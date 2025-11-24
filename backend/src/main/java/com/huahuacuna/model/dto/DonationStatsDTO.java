package com.huahuacuna.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationStatsDTO {
    private SummaryStats summary;
    private List<StatusCount> byStatus;
    private List<TypeCount> byType;
    private List<PaymentMethodCount> byPaymentMethod;
    private Map<String, MonthlyStats> byMonth;
    private List<TopDonor> topDonors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SummaryStats {
        private Long totalDonations;
        private Double totalAmount;
        private Double averageDonation;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusCount {
        private String status;
        private Long count;
        private Double total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TypeCount {
        private String donationType;
        private Long count;
        private Double total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentMethodCount {
        private String paymentMethod;
        private Long count;
        private Double total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyStats {
        private Long count;
        private Double total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopDonor {
        private String donorName;
        private String email;
        private Double totalAmount;
        private Long donationCount;
    }
}