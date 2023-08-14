package com.foodielog.application.feed.dto;

import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedRequest {

    @Getter
    public static class SaveDTO {
        private KakaoApiResponse.SearchPlace selectedSearchPlace;
        private String content;
        private boolean isLiked;
    }
}
