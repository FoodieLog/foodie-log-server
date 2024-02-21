package com.foodielog.application.notification.dto;

import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationTokenParam {
    private User user;

    private String fcmToken;
}
