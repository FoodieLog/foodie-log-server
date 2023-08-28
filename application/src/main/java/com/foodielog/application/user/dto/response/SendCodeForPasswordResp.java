package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class SendCodeForPasswordResp {
    private final String email;

    public SendCodeForPasswordResp(String email) {
        this.email = email;
    }
}