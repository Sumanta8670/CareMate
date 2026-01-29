package com.Sumanta.caremate.repository;

import com.Sumanta.caremate.entity.NotificationEntity;
import com.Sumanta.caremate.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findByUserIdAndUserRoleOrderByCreatedAtDesc(Long userId, UserRole userRole, Pageable pageable);
    Page<NotificationEntity> findByUserIdAndUserRoleAndIsReadOrderByCreatedAtDesc(Long userId, UserRole userRole, Boolean isRead, Pageable pageable);
    Long countByUserIdAndUserRoleAndIsRead(Long userId, UserRole userRole, Boolean isRead);
    Optional<NotificationEntity> findByIdAndUserIdAndUserRole(Long id, Long userId, UserRole userRole);
}