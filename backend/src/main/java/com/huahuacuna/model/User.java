package com.huahuacuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa a un usuario dentro del sistema.
 * <p>
 * Esta clase se mapea a la tabla {@code users} en la base de datos y contiene
 * la información principal de cada usuario, como sus credenciales, rol,
 * información de contacto y fechas de creación/actualización.
 * </p>
 *
 * <p>Incluye validaciones para garantizar que los campos requeridos sean válidos
 * antes de persistir los datos en la base de datos.</p>
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_reset_token", columnList = "reset_password_token")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Identificador único del usuario.
     * Se genera automáticamente mediante una estrategia de incremento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del usuario.
     * No puede ser nulo ni estar vacío, y tiene una longitud máxima de 100 caracteres.
     */
    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    /**
     * Correo electrónico único del usuario.
     * Se utiliza como identificador de inicio de sesión.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Contraseña encriptada del usuario.
     * No puede estar vacía y tiene una longitud máxima de 255 caracteres.
     */
    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * Rol asignado al usuario dentro del sistema.
     * Se almacena como String pero se maneja con el enum Role.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * Número de teléfono del usuario (opcional).
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Token para recuperación de contraseña.
     * Se genera cuando el usuario solicita restablecer su contraseña.
     */
    @Column(name = "reset_password_token", length = 100)
    private String resetPasswordToken;

    /**
     * Fecha de expiración del token de recuperación de contraseña.
     * El token solo es válido hasta esta fecha.
     */
    @Column(name = "reset_password_expires")
    private LocalDateTime resetPasswordExpires;

    /**
     * Indica si la cuenta del usuario está activa.
     * Por defecto es true, pero puede desactivarse por el administrador.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Fecha y hora en la que se creó el registro del usuario.
     * Se establece automáticamente al persistir el objeto por primera vez.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del registro del usuario.
     * Se actualiza automáticamente antes de cada modificación.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Método de ciclo de vida ejecutado automáticamente antes de insertar un nuevo usuario.
     * <p>
     * Establece las fechas de creación y actualización, y asigna el rol "APADRINADO" por defecto
     * si no se ha especificado uno.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.APADRINADO;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    /**
     * Método de ciclo de vida ejecutado automáticamente antes de actualizar el usuario.
     * <p>
     * Actualiza la fecha de modificación {@code updatedAt}.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor personalizado que permite crear un usuario con los datos principales.
     *
     * @param fullName nombre completo del usuario.
     * @param email    correo electrónico del usuario.
     * @param password contraseña del usuario.
     * @param role     rol asignado.
     * @param telefono número de teléfono (opcional).
     */
    public User(String fullName, String email, String password, Role role, String telefono) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.APADRINADO;
        this.telefono = telefono;
        this.isActive = true;
    }

    /**
     * Verifica si el token de recuperación de contraseña es válido.
     *
     * @return true si el token existe y no ha expirado
     */
    public boolean isResetTokenValid() {
        return resetPasswordToken != null
                && resetPasswordExpires != null
                && resetPasswordExpires.isAfter(LocalDateTime.now());
    }

    /**
     * Limpia el token de recuperación de contraseña.
     */
    public void clearResetToken() {
        this.resetPasswordToken = null;
        this.resetPasswordExpires = null;
    }
}