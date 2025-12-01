package com.huahuacuna.repository;

import com.huahuacuna.model.Child;
import com.huahuacuna.model.Sponsorship;
import com.huahuacuna.model.SponsorshipStatus;
import com.huahuacuna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar apadrinamientos.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {

    /**
     * Encuentra todos los apadrinamientos de un padrino.
     */
    List<Sponsorship> findByGodparent(User godparent);

    /**
     * Encuentra todos los apadrinamientos de un padrino por su ID.
     */
    List<Sponsorship> findByGodparentId(Long godparentId);

    /**
     * Encuentra el apadrinamiento activo de un padrino.
     */
    Optional<Sponsorship> findByGodparentAndStatus(User godparent, SponsorshipStatus status);

    /**
     * Encuentra el apadrinamiento activo de un padrino por su ID.
     */
    Optional<Sponsorship> findByGodparentIdAndStatus(Long godparentId, SponsorshipStatus status);

    /**
     * Encuentra todos los apadrinamientos de un niño.
     */
    List<Sponsorship> findByChild(Child child);

    /**
     * Encuentra todos los apadrinamientos de un niño por su ID.
     */
    List<Sponsorship> findByChildId(Long childId);

    /**
     * Encuentra el apadrinamiento activo de un niño.
     */
    Optional<Sponsorship> findByChildAndStatus(Child child, SponsorshipStatus status);

    /**
     * Encuentra el apadrinamiento activo de un niño por su ID.
     */
    Optional<Sponsorship> findByChildIdAndStatus(Long childId, SponsorshipStatus status);

    /**
     * Verifica si un padrino ya tiene un apadrinamiento activo.
     */
    boolean existsByGodparentIdAndStatus(Long godparentId, SponsorshipStatus status);

    /**
     * Verifica si un niño ya está apadrinado.
     */
    boolean existsByChildIdAndStatus(Long childId, SponsorshipStatus status);

    /**
     * Encuentra todos los apadrinamientos por estado.
     */
    List<Sponsorship> findByStatus(SponsorshipStatus status);

    /**
     * Cuenta los apadrinamientos activos.
     */
    long countByStatus(SponsorshipStatus status);

    /**
     * Encuentra apadrinamientos con información de padrino y niño cargada.
     */
    @Query("SELECT s FROM Sponsorship s " +
            "JOIN FETCH s.godparent " +
            "JOIN FETCH s.child " +
            "WHERE s.status = :status")
    List<Sponsorship> findByStatusWithDetails(@Param("status") SponsorshipStatus status);

    /**
     * Encuentra el apadrinamiento de un padrino con toda la información cargada.
     */
    @Query("SELECT s FROM Sponsorship s " +
            "JOIN FETCH s.godparent " +
            "JOIN FETCH s.child " +
            "WHERE s.godparent.id = :godparentId AND s.status = :status")
    Optional<Sponsorship> findByGodparentIdAndStatusWithDetails(
            @Param("godparentId") Long godparentId,
            @Param("status") SponsorshipStatus status
    );

}