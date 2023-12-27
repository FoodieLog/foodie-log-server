package com.foodielog.application.notification.dto.response;

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
    public static class ReplyNotification {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUser user;
        private final ContentReply reply;
        private final Timestamp createdAt;

        public ReplyNotification(Notification notification, ContentUser user, ContentReply reply) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.reply = reply;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class LikeNotification {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUser user;
        private final ContentFeed feed;
        private final Timestamp createdAt;

        public LikeNotification(Notification notification, ContentUser user, ContentFeed feed) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.feed = feed;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class FollowNotification {
        private final Long id;
        private final NotificationType type;
        private final Flag checkFlag;
        private final ContentUser user;
        private final Boolean isFollowed;
        private final Timestamp createdAt;

        public FollowNotification(Notification notification, ContentUser user, Boolean isFollowed) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.checkFlag = notification.getCheckFlag();
            this.user = user;
            this.isFollowed = isFollowed;
            this.createdAt = notification.getCreatedAt();
        }
    }

    @Getter
    public static class ContentUser {
        private final Long id;
        private final String nickName;
        private final String profileImgUrl;

        public ContentUser(User user) {
            this.id = user.getId();
            this.nickName = user.getNickName();
            this.profileImgUrl = user.getProfileImageUrl();
        }
    }

    @Getter
    public static class ContentFeed {
        private final Long id;
        private final String thumbnail;

        public ContentFeed(Feed feed) {
            this.id = feed.getId();
            this.thumbnail = feed.getThumbnailUrl();
        }
    }

    @Getter
    public static class ContentReply {
        private final Long id;
        private final String content;
        private final Long feedId;
        private final String thumbnail;

        public ContentReply(Reply reply) {
            this.id = reply.getId();
            this.content = reply.getContent();
            this.feedId = reply.getFeed().getId();
            this.thumbnail = reply.getFeed().getThumbnailUrl();
        }
    }
}
