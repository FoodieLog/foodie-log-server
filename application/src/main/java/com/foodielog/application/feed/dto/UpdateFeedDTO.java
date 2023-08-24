package com.foodielog.application.feed.dto;

import lombok.Getter;

public class UpdateFeedDTO {

    @Getter
    public static class Request {
        private Long feedId;
        private String content;
    }
}