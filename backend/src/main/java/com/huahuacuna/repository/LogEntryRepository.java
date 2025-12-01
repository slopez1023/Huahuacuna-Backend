package com.huahuacuna.repository;

import com.huahuacuna.model.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar entradas de bitácora.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Encuentra todas las entradas de un apadrinamiento ordenadas por fecha.
     */
    List<LogEntry> findBySponsorshipIdOrderByCreatedAtDesc(Long sponsorshipId);

    /**
     * Encuentra entradas de un apadrinamiento con paginación.
     */
    Page<LogEntry> findBySponsorshipId(Long sponsorshipId, Pageable pageable);

    /**
     * Encuentra entradas por tipo.
     */
    List<LogEntry> findBySponsorshipIdAndEntryTypeOrderByCreatedAtDesc(
            Long sponsorshipId,
            LogEntry.LogEntryType entryType
    );

    /**
     * Encuentra entradas por quién las registró.
     */
    List<LogEntry> findBySponsorshipIdAndRegisteredByOrderByCreatedAtDesc(
            Long sponsorshipId,
            LogEntry.RegisteredBy registeredBy
    );

    /**
     * Encuentra entradas en un rango de fechas.
     */
    @Query("SELECT l FROM LogEntry l " +
            "WHERE l.sponsorship.id = :sponsorshipId " +
            "AND l.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY l.createdAt DESC")
    List<LogEntry> findBySponsorshipIdAndDateRange(
            @Param("sponsorshipId") Long sponsorshipId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Cuenta las entradas de un apadrinamiento.
     */
    long countBySponsorshipId(Long sponsorshipId);

    /**
     * Encuentra las últimas N entradas de un apadrinamiento.
     */
    @Query("SELECT l FROM LogEntry l " +
            "WHERE l.sponsorship.id = :sponsorshipId " +
            "ORDER BY l.createdAt DESC " +
            "LIMIT :limit")
    List<LogEntry> findLatestBySponsorshipId(
            @Param("sponsorshipId") Long sponsorshipId,
            @Param("limit") int limit
    );

    /**
     * Busca entradas por texto en título o contenido.
     */
    @Query("SELECT l FROM LogEntry l " +
            "WHERE l.sponsorship.id = :sponsorshipId " +
            "AND (LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "     OR LOWER(l.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY l.createdAt DESC")
    List<LogEntry> searchByTitleOrContent(
            @Param("sponsorshipId") Long sponsorshipId,
            @Param("searchTerm") String searchTerm
    );

}