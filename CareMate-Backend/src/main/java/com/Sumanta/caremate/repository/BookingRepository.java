package com.Sumanta.caremate.repository;

import com.Sumanta.caremate.entity.BookingEntity;
import com.Sumanta.caremate.entity.NurseEntity;
import com.Sumanta.caremate.entity.PatientEntity;
import com.Sumanta.caremate.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    // Nurse related queries
    Page<BookingEntity> findByNurse(NurseEntity nurse, Pageable pageable);
    Page<BookingEntity> findByNurseAndStatus(NurseEntity nurse, BookingStatus status, Pageable pageable);
    List<BookingEntity> findByNurseAndStatusIn(NurseEntity nurse, List<BookingStatus> statuses);
    Optional<BookingEntity> findByIdAndNurse(Long id, NurseEntity nurse);
    Long countByNurse(NurseEntity nurse);
    Long countByNurseAndStatus(NurseEntity nurse, BookingStatus status);

    // Patient related queries
    Page<BookingEntity> findByPatient(PatientEntity patient, Pageable pageable);
    Page<BookingEntity> findByPatientAndStatus(PatientEntity patient, BookingStatus status, Pageable pageable);
    Optional<BookingEntity> findByIdAndPatient(Long id, PatientEntity patient);

    // Earnings calculations
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM BookingEntity b WHERE b.nurse = :nurse AND b.status = 'COMPLETED'")
    BigDecimal calculateTotalEarnings(@Param("nurse") NurseEntity nurse);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM BookingEntity b WHERE b.nurse = :nurse AND b.status = 'COMPLETED' AND b.completedAt >= :startDate")
    BigDecimal calculateEarningsSince(@Param("nurse") NurseEntity nurse, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM BookingEntity b WHERE b.nurse = :nurse AND b.status = 'COMPLETED' AND b.completedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateEarningsInRange(@Param("nurse") NurseEntity nurse, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Long countByNurseAndStatusAndCompletedAtAfter(NurseEntity nurse, BookingStatus status, LocalDateTime completedAt);

    Long countByNurseAndStatusAndCompletedAtBetween(NurseEntity nurse, BookingStatus status, LocalDateTime start, LocalDateTime end);

    // Check for overlapping bookings
    @Query("SELECT COUNT(b) > 0 FROM BookingEntity b WHERE b.nurse = :nurse AND b.status IN ('ACCEPTED', 'IN_PROGRESS') AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    boolean hasOverlappingBookings(@Param("nurse") NurseEntity nurse, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}