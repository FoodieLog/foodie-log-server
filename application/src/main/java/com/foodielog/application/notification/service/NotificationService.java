package com.foodielog.application.notification.service;

import com.foodielog.application.feedLike.service.FeedLikeModuleService;
import com.foodielog.application.follow.service.FollowModuleService;
import com.foodielog.application.mention.service.MentionModuleService;
import com.foodielog.application.notification.dto.NotificationTokenParam;
import com.foodielog.application.notification.service.dto.NotificationListResp;
import com.foodielog.application.reply.service.ReplyModuleService;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationModuleService notificationModuleService;
    private final ReplyModuleService replyModuleService;
    private final FeedLikeModuleService feedLikeModuleService;
    private final FollowModuleService followModuleService;
    private final MentionModuleService mentionModuleService;

    private final RedisService redisService;

    @Transactional
    public NotificationListResp getNotificationList(User user) {
        List<Notification> notificationList = notificationModuleService.getNotifications(user);

        List<Object> notificationContent = new ArrayList<>();

        for (Notification notification : notificationList) {
            NotificationType type = notification.getType();
            notification.readNotification();
            switch (type) {
                case REPLY:
                    getReplyNotification(notification, notificationContent);
                    break;
                case LIKE:
                    getLikeNotification(notification, notificationContent);
                    break;
                case FOLLOW:
                    getFollowNotification(notification, notificationContent);
                    break;
                case MENTION:
                    getMentionNotification(notification, notificationContent);
                    break;
                default:
            }
        }
        return new NotificationListResp(notificationContent);
    }

    private void getReplyNotification(Notification notification, List<Object> notificationContent) {
        replyModuleService.getNormalOptional(notification.getContentId())
                .ifPresent(reply -> {
                    NotificationListResp.ContentReplyDTO contentReply = new NotificationListResp.ContentReplyDTO(
                            reply);
                    NotificationListResp.ReplyNotificationDTO replyNotification =
                            new NotificationListResp.ReplyNotificationDTO(notification,
                                    getContentUser(reply.getUser()), contentReply);
                    notificationContent.add(replyNotification);
                });
    }

    private void getLikeNotification(Notification notification, List<Object> notificationContent) {
        feedLikeModuleService.getOptionalFeedLike(notification.getContentId())
                .ifPresent(feedLike -> {
                    NotificationListResp.ContentFeedDTO contentFeed = new NotificationListResp.ContentFeedDTO(
                            feedLike.getFeed());
                    NotificationListResp.LikeNotificationDTO likeNotification =
                            new NotificationListResp.LikeNotificationDTO(notification,
                                    getContentUser(feedLike.getUser()), contentFeed);
                    notificationContent.add(likeNotification);
                });
    }

    private void getFollowNotification(Notification notification, List<Object> notificationContent) {
        followModuleService.getOptionalFollow(notification.getContentId())
                .ifPresent(follow -> {
                    boolean isFollowed = followModuleService.isFollow(follow.getFollowedId(),
                            follow.getFollowingId());
                    NotificationListResp.FollowNotificationDTO followNotification =
                            new NotificationListResp.FollowNotificationDTO(notification,
                                    getContentUser(follow.getFollowingId()), isFollowed);
                    notificationContent.add(followNotification);
                });
    }

    private void getMentionNotification(Notification notification, List<Object> notificationContent) {
        mentionModuleService.getOptionalMention(notification.getContentId())
                .ifPresent(mention -> {
                    NotificationListResp.ContentReplyDTO contentReply = new NotificationListResp.ContentReplyDTO(
                            mention.getReply());
                    NotificationListResp.MentionNotificationDTO mentionNotification =
                            new NotificationListResp.MentionNotificationDTO(notification,
                                    getContentUser(mention.getMentioner()), contentReply);
                    notificationContent.add(mentionNotification);
                });
    }

    private NotificationListResp.ContentUserDTO getContentUser(User user) {
        return new NotificationListResp.ContentUserDTO(user);
    }

    public void registerFcmToken(NotificationTokenParam parameter) {
        User user = parameter.getUser();
        redisService.setObjectByKey(RedisService.FCM_TOKEN_PREFIX + user.getEmail(),
                parameter.getFcmToken(),
                JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);
    }
}
