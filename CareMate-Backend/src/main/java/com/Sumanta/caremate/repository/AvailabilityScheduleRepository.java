package com.Sumanta.caremate.repository;

import com.Sumanta.caremate.entity.AvailabilityScheduleEntity;
import com.Sumanta.caremate.entity.NurseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilityScheduleEntity, Long> {
    List<AvailabilityScheduleEntity> findByNurseAndIsActive(NurseEntity nurse, Boolean isActive);
    List<AvailabilityScheduleEntity> findByNurse(NurseEntity nurse);
    Optional<AvailabilityScheduleEntity> findByIdAndNurse(Long id, NurseEntity nurse);
    boolean existsByNurseAndDayOfWeekAndIsActive(NurseEntity nurse, DayOfWeek dayOfWeek, Boolean isActive);
}