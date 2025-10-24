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
        @Index(name = "idx_email", columnList = "email", unique = true)
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
     * Rol asignado al usuario dentro del sistema (por ejemplo, "USER" o "ADMIN").
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    /**
     * Número de teléfono del usuario (opcional).
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

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
     * Establece las fechas de creación y actualización, y asigna el rol "USER" por defecto
     * si no se ha especificado uno.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = "USER";
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
     * <p>
     * Si el rol no se proporciona, se asigna automáticamente el valor por defecto "USER".
     * </p>
     *
     * @param fullName nombre completo del usuario.
     * @param email    correo electrónico del usuario.
     * @param password contraseña del usuario.
     * @param role     rol asignado (puede ser nulo).
     * @param telefono número de teléfono (opcional).
     */
    public User(String fullName, String email, String password, String role, String telefono) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : "USER";
        this.telefono = telefono;
    }
}
