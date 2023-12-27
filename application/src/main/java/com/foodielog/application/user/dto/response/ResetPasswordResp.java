package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class ResetPasswordResp {
    private final String email;

    public ResetPasswordResp(String email) {
        this.email = email;
    }
}