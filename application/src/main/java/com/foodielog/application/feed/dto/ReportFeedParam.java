package com.foodielog.application.feed.dto;

import com.foodielog.server.report.type.ReportReason;
import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportFeedParam {
    private User user;

    private Long feedId;

    private ReportReason reportReason;
}