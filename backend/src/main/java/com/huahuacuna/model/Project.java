package com.huahuacuna.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal goalAmount; // Meta de dinero a recaudar
    private BigDecimal currentAmount; // Dinero recaudado actualmente

    private String imageUrl;
    @Column(nullable = false)
    private boolean published = false;

}