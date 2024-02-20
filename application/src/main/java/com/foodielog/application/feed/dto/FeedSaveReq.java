package com.foodielog.application.feed.dto;

import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server.user.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class FeedSaveReq {
    private KakaoApiResponse.SearchPlace selectedSearchPlace;
    private String content;

    @NotNull
    private Boolean isLiked;

    public FeedSaveParam toParamWith(User user, List<MultipartFile> files) {
        return FeedSaveParam.builder()
                .user(user)
                .files(files)
                .selectedSearchPlace(selectedSearchPlace)
                .content(content)
                .isLiked(isLiked)
                .build();
    }
}