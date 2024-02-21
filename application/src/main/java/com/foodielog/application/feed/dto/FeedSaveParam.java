package com.foodielog.application.feed.dto;

import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Getter
public class FeedSaveParam {
    private User user;

    private List<MultipartFile> files;

    private KakaoApiResponse.SearchPlace selectedSearchPlace;

    private String content;

    private Boolean isLiked;
}