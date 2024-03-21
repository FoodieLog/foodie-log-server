package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.type.UserStatus;
import lombok.Getter;

@Getter
public class ExistsKakaoResp {

    private final String kakaoAccessToken;
    private final Boolean isExists;
    private final UserStatus status;

    public ExistsKakaoResp(String kakaoAccessToken, Boolean isExists, UserStatus status) {
        this.kakaoAccessToken = kakaoAccessToken;
        this.isExists = isExists;
        this.status = status;
    }
}
