package com.Sumanta.caremate.repository;

import com.Sumanta.caremate.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByEmail(String email);
    Optional<PatientEntity> findByMobileNo(String mobileNo);
    Optional<PatientEntity> findByFullNameAndMobileNoAndEmail(String fullName, String mobileNo, String email);
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
}