package com.foodielog.management.report.controller.dto;

import com.foodielog.server.admin.type.ProcessedStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessParam {

    private Long reportedId;

    private Long contentId;

    private ProcessedStatus status;
}
