package com.foodielog.application.feed.dto;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UpdateFeedReq {
    @NotBlank
    private String content;

    public UpdateFeedParam toParamWith(User user, Long feedId) {
        return UpdateFeedParam.builder()
                .user(user)
                .feedId(feedId)
                .content(content)
                .build();
    }
}