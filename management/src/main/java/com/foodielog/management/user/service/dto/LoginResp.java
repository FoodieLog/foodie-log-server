package com.foodielog.management.user.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResp {
    private final String nickName;
    private final String accessToken;

    @JsonIgnore
    private final String refreshToken;

    public LoginResp(User user, String accessToken, String refreshToken) {
        this.nickName = user.getNickName();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
