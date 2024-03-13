package com.foodielog.application.notification.service;

import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.repository.NotificationRepository;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationModuleService {
    private final NotificationRepository notificationRepository;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(User user) {
        return notificationRepository.findByUserOrderByIdDesc(user);
    }
}
