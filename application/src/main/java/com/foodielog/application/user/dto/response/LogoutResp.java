package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class LogoutResp {
    private final String email;
    private final Boolean success;

    public LogoutResp(String email, Boolean success) {
        this.email = email;
        this.success = success;
    }
}
