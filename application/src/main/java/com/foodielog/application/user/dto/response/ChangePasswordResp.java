package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class ChangePasswordResp {
    private final String password;

    public ChangePasswordResp(String password) {
        this.password = password;
    }
}
