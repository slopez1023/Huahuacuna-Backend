package com.huahuacuna.repository;

import com.huahuacuna.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    // Filtrar por rango de fechas
    List<Donation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Filtrar por estado
    List<Donation> findByStatus(String status);

    // Filtrar por tipo de donación
    List<Donation> findByDonationType(String donationType);

    // Filtrar por múltiples criterios
    @Query("SELECT d FROM Donation d WHERE " +
            "(:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate) AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:type IS NULL OR d.donationType = :type)")
    List<Donation> findByFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("type") String type
    );

    // Estadísticas por estado
    @Query("SELECT d.status as status, COUNT(d) as count, COALESCE(SUM(d.amount), 0) as total " +
            "FROM Donation d " +
            "WHERE (:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate) " +
            "GROUP BY d.status")
    List<Object[]> countByStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Estadísticas por tipo
    @Query("SELECT d.donationType as type, COUNT(d) as count, COALESCE(SUM(d.amount), 0) as total " +
            "FROM Donation d " +
            "WHERE (:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate) " +
            "GROUP BY d.donationType")
    List<Object[]> countByType(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Estadísticas por método de pago
    @Query("SELECT d.paymentMethod as method, COUNT(d) as count, COALESCE(SUM(d.amount), 0) as total " +
            "FROM Donation d " +
            "WHERE d.donationType = 'monetaria' AND " +
            "(:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate) " +
            "GROUP BY d.paymentMethod")
    List<Object[]> countByPaymentMethod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Top donantes
    @Query("SELECT d.donorName as name, d.email as email, " +
            "SUM(d.amount) as total, COUNT(d) as count " +
            "FROM Donation d " +
            "WHERE d.donationType = 'monetaria' AND " +
            "(:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate) " +
            "GROUP BY d.donorName, d.email " +
            "ORDER BY SUM(d.amount) DESC")
    List<Object[]> findTopDonors(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Total de donaciones
    @Query("SELECT COUNT(d) FROM Donation d " +
            "WHERE (:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate)")
    Long countDonations(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Suma total de montos
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d " +
            "WHERE d.donationType = 'monetaria' AND " +
            "(:startDate IS NULL OR d.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR d.createdAt <= :endDate)")
    Double sumTotalAmount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}