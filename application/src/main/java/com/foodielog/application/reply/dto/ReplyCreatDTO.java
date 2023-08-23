package com.foodielog.application.reply.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.reply.entity.Reply;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

public class ReplyCreatDTO {
    @Getter
    public static class Request {
        @NotBlank(message = "내용이 공백일 수 없습니다.")
        @Size(max = 150, message = "최대 글자수는 150 입니다.")
        private String content;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String nickName;
        private final String content;
        private final String profileImageUrl;
        private final Timestamp createdAt;

        public Response(Reply reply) {
            this.id = reply.getId();
            this.nickName = reply.getUser().getNickName();
            this.content = reply.getContent();
            this.profileImageUrl = reply.getUser().getProfileImageUrl();
            this.createdAt = reply.getCreatedAt();
        }
    }

    @Getter
    public static class ListDTO {
        private final String nickName;
        private final String profileImageUrl;
        private final String content;
        private final Timestamp createdAt;
        private final List<ReplyDTO> replyList;

        public ListDTO(Feed feed, List<ReplyDTO> replyListDTO) {
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
        private final String nickName;
        private final String profileImageUrl;
        private final String content;
        private final Timestamp createdAt;

        public ReplyDTO(Reply reply) {
            this.id = reply.getId();
            this.nickName = reply.getUser().getNickName();
            this.profileImageUrl = reply.getUser().getProfileImageUrl();
            this.content = reply.getContent();
            this.createdAt = reply.getCreatedAt();
        }
    }
}