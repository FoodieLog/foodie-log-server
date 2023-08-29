package com.foodielog.application.feed.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class UpdateFeedReq {
    @NotNull(message = "값이 null 일 수 없습니다.")
    @Positive(message = "양수만 가능합니다.")
    private Long feedId;

    @NotBlank
    private String content;
}