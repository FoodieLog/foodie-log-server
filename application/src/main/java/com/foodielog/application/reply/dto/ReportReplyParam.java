package com.foodielog.application.reply.dto;

import com.foodielog.server.report.type.ReportReason;
import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportReplyParam {
    private User user;

    private Long replyId;

    private ReportReason reportReason;
}
