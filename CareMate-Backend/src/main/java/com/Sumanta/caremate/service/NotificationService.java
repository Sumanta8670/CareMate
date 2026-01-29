package com.Sumanta.caremate.service;

import com.Sumanta.caremate.entity.NotificationEntity;
import com.Sumanta.caremate.enums.NotificationType;
import com.Sumanta.caremate.enums.UserRole;
import com.Sumanta.caremate.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(Long userId, UserRole userRole, NotificationType type,
                                   String title, String message, Long relatedEntityId) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(userId);
        notification.setUserRole(userRole);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
        log.info("Notification created for user {} with role {}: {}", userId, userRole, title);
    }
}