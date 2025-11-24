package com.huahuacuna.model.dto;

import com.huahuacuna.model.ApplicationRequest;
import com.huahuacuna.model.ApplicationStatus;
import com.huahuacuna.model.ApplicationType;

import java.time.LocalDateTime;

/**
 * DTO para enviar información de solicitudes al frontend.
 * No expone información sensible innecesaria.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public class ApplicationResponseDTO {

    private Long id;
    private ApplicationType type;
    private ApplicationStatus status;
    private String fullName;
    private String email;
    private String phone;

    // Campos de voluntarios
    private String interestArea;
    private String availability;
    private String previousExperience;
    private Boolean acceptsInformation;

    // Campos de padrinos
    private String country;
    private String idNumber;
    private String idDocumentPath;

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    private String adminComments;

    // Constructor vacío
    public ApplicationResponseDTO() {
    }

    /**
     * Constructor desde entidad ApplicationRequest
     *
     * @param application Entidad de solicitud
     */
    public ApplicationResponseDTO(ApplicationRequest application) {
        this.id = application.getId();
        this.type = application.getType();
        this.status = application.getStatus();
        this.fullName = application.getFullName();
        this.email = application.getEmail();
        this.phone = application.getPhone();
        this.interestArea = application.getInterestArea();
        this.availability = application.getAvailability();
        this.previousExperience = application.getPreviousExperience();
        this.acceptsInformation = application.getAcceptsInformation();
        this.country = application.getCountry();
        this.idNumber = application.getIdNumber();
        this.idDocumentPath = application.getIdDocumentPath();
        this.createdAt = application.getCreatedAt();
        this.updatedAt = application.getUpdatedAt();
        this.reviewedAt = application.getReviewedAt();
        this.adminComments = application.getAdminComments();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationType getType() {
        return type;
    }

    public void setType(ApplicationType type) {
        this.type = type;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInterestArea() {
        return interestArea;
    }

    public void setInterestArea(String interestArea) {
        this.interestArea = interestArea;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPreviousExperience() {
        return previousExperience;
    }

    public void setPreviousExperience(String previousExperience) {
        this.previousExperience = previousExperience;
    }

    public Boolean getAcceptsInformation() {
        return acceptsInformation;
    }

    public void setAcceptsInformation(Boolean acceptsInformation) {
        this.acceptsInformation = acceptsInformation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdDocumentPath() {
        return idDocumentPath;
    }

    public void setIdDocumentPath(String idDocumentPath) {
        this.idDocumentPath = idDocumentPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }
}