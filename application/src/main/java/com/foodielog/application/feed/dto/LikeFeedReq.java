package com.foodielog.application.feed.dto;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class LikeFeedReq {
    @NotNull(message = "값이 null 일 수 없습니다.")
    @Positive(message = "양수만 가능합니다.")
    private Long feedId;

    public LikeFeedParam toParamWith(User user) {
        return LikeFeedParam.builder()
                .user(user)
                .feedId(feedId)
                .build();
    }
}