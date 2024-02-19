package com.foodielog.application.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResetPasswordParam {
    private String email;

    private String password;
}