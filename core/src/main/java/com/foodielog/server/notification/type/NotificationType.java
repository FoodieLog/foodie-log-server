package com.foodielog.server.notification.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NotificationType {
    REPLY("댓글"),
    LIKE("좋아요"),
    FOLLOW("팔로우");

    private final String label;
}
