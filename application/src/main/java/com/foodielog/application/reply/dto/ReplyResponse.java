package com.foodielog.application.reply.dto;

import com.foodielog.server.reply.entity.Reply;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

public class ReplyResponse {

    @Builder
    @Getter
    public static class createDTO {
        private Long id;
        private String nickName;
        private String content;
        private String profileImageUrl;
        private Timestamp createdAt;

        public static createDTO from(Reply reply) {
            return createDTO.builder()
                    .id(reply.getId())
                    .nickName(reply.getUser().getNickName())
                    .content(reply.getContent())
                    .profileImageUrl(reply.getUser().getProfileImageUrl())
                    .createdAt(reply.getCreatedAt())
                    .build();
        }
    }
}
