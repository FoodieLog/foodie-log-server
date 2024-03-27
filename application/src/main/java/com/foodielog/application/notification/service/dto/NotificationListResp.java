package com.foodielog.application.notification.service.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class NotificationListResp {
    private final List<?> content;

    public NotificationListResp(List<?> content) {
        this.content = content;
    }

    @Getter
    public static class ReplyNotificationDTO {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUserDTO user;
        private final ContentReplyDTO reply;
        private final Timestamp createdAt;

        public ReplyNotificationDTO(Notification notification, ContentUserDTO user, ContentReplyDTO reply) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.reply = reply;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class LikeNotificationDTO {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUserDTO user;
        private final ContentFeedDTO feed;
        private final Timestamp createdAt;

        public LikeNotificationDTO(Notification notification, ContentUserDTO user, ContentFeedDTO feed) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.feed = feed;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class FollowNotificationDTO {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUserDTO user;
        private final Boolean isFollowed;
        private final Timestamp createdAt;

        public FollowNotificationDTO(Notification notification, ContentUserDTO user, Boolean isFollowed) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.isFollowed = isFollowed;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class MentionNotificationDTO {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUserDTO user;
        private final ContentReplyDTO reply;
        private final Timestamp createdAt;

        public MentionNotificationDTO(Notification notification, ContentUserDTO user, ContentReplyDTO reply) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.reply = reply;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class ContentUserDTO {
        private final Long id;
        private final String nickName;
        private final String profileImgUrl;

        public ContentUserDTO(User user) {
            this.id = user.getId();
            this.nickName = user.getNickName();
            this.profileImgUrl = user.getProfileImageUrl();
        }
    }

    @Getter
    public static class ContentFeedDTO {
        private final Long id;
        private final String thumbnail;

        public ContentFeedDTO(Feed feed) {
            this.id = feed.getId();
            this.thumbnail = feed.getThumbnailUrl();
        }
    }

    @Getter
    public static class ContentReplyDTO {
        private final Long id;
        private final String content;
        private final Long feedId;
        private final String thumbnail;

        public ContentReplyDTO(Reply reply) {
            this.id = reply.getId();
            this.content = reply.getContent();
            this.feedId = reply.getFeed().getId();
            this.thumbnail = reply.getFeed().getThumbnailUrl();
        }
    }
}
