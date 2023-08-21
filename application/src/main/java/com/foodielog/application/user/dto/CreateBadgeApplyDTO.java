package com.foodielog.application.user.dto;

import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.user.entity.User;
import lombok.Getter;

import java.sql.Timestamp;

public class CreateBadgeApplyDTO {
    @Getter
    public static class Response {
        private final String nickName;
        private final Timestamp createdAt;

        public Response(User user, BadgeApply badgeApply) {
            this.nickName = user.getNickName();
            this.createdAt = badgeApply.getCreatedAt();
        }
    }
}
