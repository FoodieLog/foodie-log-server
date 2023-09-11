package com.foodielog.server.notification.repository;

import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}