package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class SendCodeForSignupResp {
    private final String email;

    public SendCodeForSignupResp(String email) {
        this.email = email;
    }
}