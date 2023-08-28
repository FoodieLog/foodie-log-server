package com.foodielog.application.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResp {
    private final Long id;
    private final String nickName;
    private final String profileImageUrl;
    private final String accessToken;

    @JsonIgnore
    private final String refreshToken;

    public LoginResp(User user, String accessToken, String refreshToken) {
        this.id = user.getId();
        this.nickName = user.getNickName();
        this.profileImageUrl = user.getProfileImageUrl();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
