package com.foodielog.application._core.fcm;

import com.foodielog.server._core.redis.RedisService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FcmMessageProvider {
    private final RedisService redisService;

    public void sendReplyMessage(String email, String replyUser) {
        String key = getKey(email);

        if (!hasKey(key)) {
            return;
        }

        String token = getToken(key);
        Message message = Message.builder()
                .putData("title", "댓글 알림")
                .putData("content", replyUser + "님이 댓글을 남겼습니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void sendLikeMessage(String email, String likeUser) {
        String key = getKey(email);

        if (!hasKey(key)) {
            return;
        }

        String token = getToken(key);
        Message message = Message.builder()
                .putData("title", "좋아요 알림")
                .putData("content", likeUser + "님이 게시글을 좋아합니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void sendFollowMessage(String email, String followUser) {
        String key = getKey(email);

        if (!hasKey(key)) {
            return;
        }

        String token = getToken(key);
        Message message = Message.builder()
                .putData("title", "팔로우 알림")
                .putData("content", followUser + "님이 팔로우 하셨습니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void send(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
    }

    private boolean hasKey(String email) {
        return redisService.hasKey(email);
    }

    private String getKey(String email) {
        return RedisService.FCM_TOKEN_PREFIX + email;
    }

    private String getToken(String key) {
        return redisService.getObjectByKey(key, String.class);
    }
}
