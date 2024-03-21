package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class VerifiedCodeResp {
    private final String email;
    private final String code;
    private final Boolean isVerified;

    public VerifiedCodeResp(String email, String code, Boolean isVerified) {
        this.email = email;
        this.code = code;
        this.isVerified = isVerified;
    }
}