package com.foodielog.application.user.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class ReissueReq {
    @NotEmpty(message = "accessToken 을 입력해주세요.")
    private String accessToken;

    @NotEmpty(message = "refreshToken 을 입력해주세요.")
    private String refreshToken;
}