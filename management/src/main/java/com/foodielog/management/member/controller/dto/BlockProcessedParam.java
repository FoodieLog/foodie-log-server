package com.foodielog.management.member.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockProcessedParam {

    private Long userId;

    private String reason;
}