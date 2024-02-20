package com.foodielog.application.reply.service.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.reply.entity.Reply;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class ReplyCreateResp {
    private final Long id;
    private final String nickName;
    private final String content;
    private final String profileImageUrl;
    private final Timestamp createdAt;

    public ReplyCreateResp(Reply reply) {
        this.id = reply.getId();
        this.nickName = reply.getUser().getNickName();
        this.content = reply.getContent();
        this.profileImageUrl = reply.getUser().getProfileImageUrl();
        this.createdAt = reply.getCreatedAt();
    }

    @Getter
    public static class ListDTO {
        private final Long userId;
        private final String nickName;
        private final String profileImageUrl;
        private final String content;
        private final Timestamp createdAt;
        private final List<ReplyDTO> replyList;

        public ListDTO(Feed feed, List<ReplyDTO> replyListDTO) {
            this.userId = feed.getUser().getId();
            this.nickName = feed.getUser().getNickName();
            this.profileImageUrl = feed.getUser().getProfileImageUrl();
            this.content = feed.getContent();
            this.createdAt = feed.getCreatedAt();
            this.replyList = replyListDTO;
        }
    }

    @Getter
    public static class ReplyDTO {
        private final Long id;
        private final Long userId;
        private final String nickName;
        private final String profileImageUrl;
        private final String content;
        private final Timestamp createdAt;

        public ReplyDTO(Reply reply) {
            this.id = reply.getId();
            this.userId = reply.getUser().getId();
            this.nickName = reply.getUser().getNickName();
            this.profileImageUrl = reply.getUser().getProfileImageUrl();
            this.content = reply.getContent();
            this.createdAt = reply.getCreatedAt();
        }
    }
}
