package com.foodielog.application.user.dto;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

import java.sql.Timestamp;

public class CheckBadgeApplyDTO {
    @Getter
    public static class Response {
        private final String nickName;
        private final Flag flag;
        private final Timestamp createdAt;

        public Response(User user, Timestamp createdAt) {
            this.nickName = user.getNickName();
            this.flag = createdAt != null ? Flag.Y : Flag.N;
            this.createdAt = createdAt;
        }
    }
}
