package com.foodielog.application.notification.service;

import com.foodielog.application.feedLike.service.FeedLikeModuleService;
import com.foodielog.application.follow.service.FollowModuleService;
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
                default:
            }
        }
        return new NotificationListResp(notificationContent);
    }

    private void getReplyNotification(Notification notification, List<Object> notificationContent) {
        replyModuleService.findByIdAndStatusOp(notification.getContentId())
                .ifPresent(reply -> {
                    NotificationListResp.ContentReply contentReply = new NotificationListResp.ContentReply(reply);
                    NotificationListResp.ReplyNotification replyNotification =
                            new NotificationListResp.ReplyNotification(notification, getContentUser(reply.getUser()), contentReply);
                    notificationContent.add(replyNotification);
                });
    }

    private void getLikeNotification(Notification notification, List<Object> notificationContent) {
        feedLikeModuleService.getOptionalFeedLike(notification.getContentId())
                .ifPresent(feedLike -> {
                    NotificationListResp.ContentFeed contentFeed = new NotificationListResp.ContentFeed(feedLike.getFeed());
                    NotificationListResp.LikeNotification likeNotification =
                            new NotificationListResp.LikeNotification(notification, getContentUser(feedLike.getUser()), contentFeed);
                    notificationContent.add(likeNotification);
                });
    }

    private void getFollowNotification(Notification notification, List<Object> notificationContent) {
        followModuleService.getOptionalFollow(notification.getContentId())
                .ifPresent(follow -> {
                    boolean isFollowed = followModuleService.isFollow(follow.getFollowedId(), follow.getFollowingId());
                    NotificationListResp.FollowNotification followNotification =
                            new NotificationListResp.FollowNotification(notification, getContentUser(follow.getFollowingId()), isFollowed);
                    notificationContent.add(followNotification);
                });
    }

    private NotificationListResp.ContentUser getContentUser(User user) {
        return new NotificationListResp.ContentUser(user);
    }

    public void registerFcmToken(NotificationTokenParam parameter) {
        User user = parameter.getUser();
        redisService.setObjectByKey(RedisService.FCM_TOKEN_PREFIX + user.getEmail(), parameter.getFcmToken(),
                JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);
    }
}
