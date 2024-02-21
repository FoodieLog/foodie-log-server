package com.foodielog.application.notification.dto;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class NotificationTokenReq {
    @NotBlank(message = "내용이 공백일 수 없습니다.")
    private String fcmToken;

    public NotificationTokenParam toParamWith(User user) {
        return NotificationTokenParam.builder()
                .user(user)
                .fcmToken(fcmToken)
                .build();
    }
}
