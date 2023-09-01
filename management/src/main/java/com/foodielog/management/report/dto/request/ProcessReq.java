package com.foodielog.management.report.dto.request;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.admin.type.ProcessedStatus;
import lombok.Getter;

@Getter
public class ProcessReq {
    private Long reportedId;
    private Long contentId;

    @ValidEnum(enumClass = ProcessedStatus.class)
    private ProcessedStatus status;
}
