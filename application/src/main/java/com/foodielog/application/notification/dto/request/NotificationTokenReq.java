package com.foodielog.application.notification.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class NotificationTokenReq {
    @NotBlank(message = "내용이 공백일 수 없습니다.")
    private String fcmToken;
}
