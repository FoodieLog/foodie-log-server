package com.foodielog.application.notification.service;

import com.foodielog.application.notification.dto.request.NotificationTokenReq;
import com.foodielog.application.notification.dto.response.NotificationListResp;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.repository.NotificationRepository;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ReplyRepository replyRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FollowRepository followRepository;

    private final RedisService redisService;

    @Transactional(readOnly = true)
    public NotificationListResp getNotificationList(User user) {
        List<Notification> notificationList = notificationRepository.findByUser(user);

        List<Object> notificationContent = new ArrayList<>();

        for (Notification notification : notificationList) {
            NotificationType type = notification.getType();
            switch (type) {
                case REPLY:
                    NotificationListResp.ReplyNotification replyNotification = getReplyNotification(notification);
                    notificationContent.add(replyNotification);
                    break;
                case LIKE:
                    NotificationListResp.LikeNotification likeNotification = getLikeNotification(notification);
                    notificationContent.add(likeNotification);
                    break;
                case FOLLOW:
                    NotificationListResp.FollowNotification followNotification = getFollowNotification(notification);
                    notificationContent.add(followNotification);
                    break;
                default:
            }
        }
        return new NotificationListResp(notificationContent);
    }

    private NotificationListResp.ReplyNotification getReplyNotification(Notification notification) {
        Reply reply = replyRepository.findByIdAndStatus(notification.getContentId(), ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 댓글이 존재하지 않습니다"));

        NotificationListResp.ContentReply contentReply = new NotificationListResp.ContentReply(reply);

        return new NotificationListResp.ReplyNotification(notification, getContentUser(reply.getUser()), contentReply);
    }

    private NotificationListResp.LikeNotification getLikeNotification(Notification notification) {
        FeedLike feedLike = feedLikeRepository.findById(notification.getContentId())
                .orElseThrow(() -> new Exception404("좋아요가 존재하지 않습니다."));

        NotificationListResp.ContentFeed contentFeed = new NotificationListResp.ContentFeed(feedLike.getFeed());

        return new NotificationListResp.LikeNotification(notification, getContentUser(feedLike.getUser()), contentFeed);
    }

    private NotificationListResp.FollowNotification getFollowNotification(Notification notification) {
        Follow follow = followRepository.findById(notification.getContentId())
                .orElseThrow(() -> new Exception404("팔로우가 존재하지 않습니다."));

        boolean isFollowed = followRepository.existsByFollowingIdAndFollowedId(follow.getFollowedId(), follow.getFollowingId());

        return new NotificationListResp.FollowNotification(notification, getContentUser(follow.getFollowingId()), isFollowed);
    }

    private NotificationListResp.ContentUser getContentUser(User user) {
        return new NotificationListResp.ContentUser(user);
    }

    public void registerFcmToken(User user, NotificationTokenReq notificationTokenReq) {
        redisService.setObjectByKey(RedisService.FCM_TOKEN_PREFIX + user.getEmail(), notificationTokenReq.getFcmToken(),
                JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);
    }
}
