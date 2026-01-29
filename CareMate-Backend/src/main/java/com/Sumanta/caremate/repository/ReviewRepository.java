package com.Sumanta.caremate.repository;

import com.Sumanta.caremate.entity.NurseEntity;
import com.Sumanta.caremate.entity.PatientEntity;
import com.Sumanta.caremate.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // Nurse related queries
    Page<ReviewEntity> findByNurse(NurseEntity nurse, Pageable pageable);
    Optional<ReviewEntity> findByIdAndNurse(Long id, NurseEntity nurse);
    Long countByNurse(NurseEntity nurse);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.nurse = :nurse")
    Double calculateAverageRating(@Param("nurse") NurseEntity nurse);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.nurse = :nurse AND r.rating = :rating")
    Long countByNurseAndRating(@Param("nurse") NurseEntity nurse, @Param("rating") Integer rating);

    // Patient related queries
    Page<ReviewEntity> findByPatient(PatientEntity patient, Pageable pageable);
    Optional<ReviewEntity> findByIdAndPatient(Long id, PatientEntity patient);
    boolean existsByBookingId(Long bookingId);
}