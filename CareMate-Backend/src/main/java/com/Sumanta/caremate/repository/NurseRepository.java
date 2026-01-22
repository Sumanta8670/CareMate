package com.Sumanta.caremate.repository;

import com.Sumanta.caremate.entity.NurseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NurseRepository extends JpaRepository<NurseEntity, Long> {
    Optional<NurseEntity> findByEmail(String email);
    Optional<NurseEntity> findByMobileNo(String mobileNo);
    Optional<NurseEntity> findByFullNameAndMobileNoAndEmail(String fullName, String mobileNo, String email);
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
}