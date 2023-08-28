package com.foodielog.application.feed.dto.request;

import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class FeedSaveReq {
    private KakaoApiResponse.SearchPlace selectedSearchPlace;
    private String content;

    @NotBlank
    private Boolean isLiked;
}