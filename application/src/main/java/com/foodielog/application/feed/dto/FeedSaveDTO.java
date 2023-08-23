package com.foodielog.application.feed.dto;

import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedSaveDTO {

    @Getter
    public static class Request {
        private KakaoApiResponse.SearchPlace selectedSearchPlace;
        private String content;

        @NotBlank
        private Boolean isLiked;
    }
}