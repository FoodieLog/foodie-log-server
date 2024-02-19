package com.foodielog.application.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginParam {
    private String email;

    private String password;
}
