package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class ExistsKakaoResp {
    private final String kakaoAccessToken;
    private final Boolean isExists;

    public ExistsKakaoResp(String kakaoAccessToken, Boolean isExists) {
        this.kakaoAccessToken = kakaoAccessToken;
        this.isExists = isExists;
    }
}
