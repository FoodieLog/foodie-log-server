package com.foodielog.application.feed.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UpdateFeedReq {

    @NotBlank
    private String content;
}