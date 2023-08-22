package com.foodielog.application.feed.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LikeFeedDTO {

    @Getter
    public static class Request {
        @NotNull(message = "값이 null 일 수 없습니다.")
        @Min(value = 1, message = "양수만 가능합니다.")
        private Long feedId;
    }
}