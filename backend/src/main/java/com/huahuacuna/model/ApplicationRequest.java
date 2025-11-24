package com.huahuacuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidad que representa una solicitud de participación en la fundación.
 * Puede ser para voluntariado o para apadrinamiento.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Entity
@Table(name = "application_requests")
public class ApplicationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de solicitud: VOLUNTARIO o PADRINO
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de solicitud es obligatorio")
    @Column(nullable = false, length = 20)
    private ApplicationType type;

    /**
     * Estado actual de la solicitud
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDIENTE;

    // ========== CAMPOS COMUNES ==========

    /**
     * Nombre completo del solicitante
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String fullName;

    /**
     * Correo electrónico del solicitante
     */
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Número de teléfono del solicitante
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    @Column(nullable = false, length = 20)
    private String phone;

    // ========== CAMPOS ESPECÍFICOS DE VOLUNTARIOS ==========

    /**
     * Área de interés del voluntario (educación, salud, logística, etc.)
     */
    @Column(length = 100)
    private String interestArea;

    /**
     * Disponibilidad horaria del voluntario
     */
    @Column(columnDefinition = "TEXT")
    private String availability;

    /**
     * Experiencia previa del voluntario
     */
    @Column(columnDefinition = "TEXT")
    private String previousExperience;

    /**
     * Indica si acepta recibir información sobre actividades
     */
    @Column(nullable = true)
    private Boolean acceptsInformation;

    // ========== CAMPOS ESPECÍFICOS DE PADRINOS ==========

    /**
     * País de residencia del padrino
     */
    @Column(length = 100)
    private String country;

    /**
     * Número de cédula de ciudadanía o documento de identidad
     */
    @Column(length = 50)
    private String idNumber;

    /**
     * Ruta o URL del documento de identidad escaneado
     * (almacenado en el sistema de archivos o servicio cloud)
     */
    @Column(length = 500)
    private String idDocumentPath;

    // ========== CAMPOS DE AUDITORÍA ==========

    /**
     * Fecha y hora de creación de la solicitud
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * ID del administrador que revisó la solicitud
     */
    @Column
    private Long reviewedBy;

    /**
     * Fecha y hora de revisión
     */
    @Column
    private LocalDateTime reviewedAt;

    /**
     * Comentarios del administrador sobre la solicitud
     */
    @Column(columnDefinition = "TEXT")
    private String adminComments;

    // ========== MÉTODOS DE CICLO DE VIDA JPA ==========

    /**
     * Se ejecuta antes de persistir la entidad por primera vez
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.PENDIENTE;
        }
    }

    /**
     * Se ejecuta antes de actualizar la entidad
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== CONSTRUCTORES ==========

    public ApplicationRequest() {
    }

    /**
     * Constructor para solicitudes de voluntariado
     */
    public ApplicationRequest(String fullName, String email, String phone,
                              String interestArea, String availability,
                              String previousExperience, Boolean acceptsInformation) {
        this.type = ApplicationType.VOLUNTARIO;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.interestArea = interestArea;
        this.availability = availability;
        this.previousExperience = previousExperience;
        this.acceptsInformation = acceptsInformation;
        this.status = ApplicationStatus.PENDIENTE;
    }

    /**
     * Constructor para solicitudes de apadrinamiento
     */
    public ApplicationRequest(String fullName, String email, String phone,
                              String country, String idNumber, String idDocumentPath) {
        this.type = ApplicationType.PADRINO;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.idNumber = idNumber;
        this.idDocumentPath = idDocumentPath;
        this.status = ApplicationStatus.PENDIENTE;
    }

    // ========== GETTERS Y SETTERS ==========

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

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
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

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Verifica si esta solicitud es de tipo voluntario
     * @return true si es voluntario, false en caso contrario
     */
    public boolean isVolunteer() {
        return this.type == ApplicationType.VOLUNTARIO;
    }

    /**
     * Verifica si esta solicitud es de tipo padrino
     * @return true si es padrino, false en caso contrario
     */
    public boolean isSponsor() {
        return this.type == ApplicationType.PADRINO;
    }

    /**
     * Verifica si la solicitud está pendiente
     * @return true si está pendiente, false en caso contrario
     */
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDIENTE;
    }

    @Override
    public String toString() {
        return "ApplicationRequest{" +
                "id=" + id +
                ", type=" + type +
                ", status=" + status +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}