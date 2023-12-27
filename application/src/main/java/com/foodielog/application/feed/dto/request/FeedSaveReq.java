package com.foodielog.application.feed.dto.request;

import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class FeedSaveReq {
    private KakaoApiResponse.SearchPlace selectedSearchPlace;
    private String content;

    @NotNull
    private Boolean isLiked;
}