package com.foodielog.application.feed.dto.request;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.report.type.ReportReason;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class ReportFeedReq {
    @NotNull(message = "값이 null 일 수 없습니다.")
    @Positive(message = "양수만 가능합니다.")
    private Long feedId;

    @ValidEnum(enumClass = ReportReason.class)
    private ReportReason reportReason;
}