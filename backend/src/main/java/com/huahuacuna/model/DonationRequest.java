package com.huahuacuna.model;

import lombok.Data;

@Data
public class DonationRequest {
    private Double amount;
    private String fullName;
    private String email;
    private String phone;
    private String donationType;
    private String paymentMethod;
    private String description;
    private String itemType;
}