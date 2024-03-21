package com.foodielog.application.user.controller.dto;

import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangePasswordParam {
    private User user;

    private String oldPassword;

    private String newPassword;
}

