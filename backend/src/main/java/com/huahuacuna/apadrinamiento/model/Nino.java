
package com.huahuacuna.apadrinamiento.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data; // <-- Importante
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ninos") // Nombre de la tabla en la BD
@Data // <-- ESTA ANOTACIÓN CREA TODOS LOS GETTERS, SETTERS, toString, etc.
@NoArgsConstructor // <-- Crea un constructor vacío


public class Nino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    private LocalDate fechaNacimiento;

    @Lob // Para texto largo
    private String historia;

    private String urlFotoPrincipal;

    @Enumerated(EnumType.STRING) // Guarda el string (DISPONIBLE) en lugar de un número (0)
    @Column(nullable = false)
    private EstadoNino estado;

    public Nino(String nombres, String apellidos, LocalDate fechaNacimiento, String historia, String urlFotoPrincipal, EstadoNino estado) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.historia = historia;
        this.urlFotoPrincipal = urlFotoPrincipal;
        this.estado = estado;
    }

    // Getters, Setters, Constructores...
}