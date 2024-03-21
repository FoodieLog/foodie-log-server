package com.foodielog.management.member.service.dto;

import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.type.ProcessedStatus;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class BadgeApplyListResp {
    private final List<BadgeApplyMemberDTO> content;

    public BadgeApplyListResp(List<BadgeApplyMemberDTO> content) {
        this.content = content;
    }

    @Getter
    public static class BadgeApplyMemberDTO {
        private final Long badgeApplyId;
        private final String nickName;
        private final String email;
        private final Long feedCount;
        private final Long replyCount;
        private final Long followerCount;
        private final Timestamp applyAt;
        private final ProcessedStatus processedStatus;

        public BadgeApplyMemberDTO(BadgeApply badgeApply, Long feedCount, Long replyCount, Long followerCount) {
            this.badgeApplyId = badgeApply.getId();
            this.nickName = badgeApply.getUser().getNickName();
            this.email = badgeApply.getUser().getEmail();
            this.feedCount = feedCount;
            this.replyCount = replyCount;
            this.followerCount = followerCount;
            this.applyAt = badgeApply.getCreatedAt();
            this.processedStatus = badgeApply.getStatus();
        }
    }
}
