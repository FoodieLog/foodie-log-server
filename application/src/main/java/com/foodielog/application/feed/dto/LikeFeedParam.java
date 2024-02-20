package com.foodielog.application.feed.dto;

import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeFeedParam {
    private User user;

    private Long feedId;
}