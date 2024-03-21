package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class SignUpResp {
    private final String email;
    private final String nickName;
    private final String profileImageUrl;

    public SignUpResp(String email, String nickName, String profileImageUrl) {
        this.email = email;
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
    }
}